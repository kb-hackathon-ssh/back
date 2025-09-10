package kb.hackathon.ssh.domain.atm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kb.hackathon.ssh.domain.atm.client.KakaoLocalClient;
import kb.hackathon.ssh.domain.atm.client.dto.KakaoLocalSearchResponse;
import kb.hackathon.ssh.domain.atm.dto.AtmDto;
import kb.hackathon.ssh.domain.atm.dto.AtmType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AtmService {

    private final KakaoLocalClient kakao;
    private static final Logger log = LoggerFactory.getLogger(AtmService.class);

    public AtmService(KakaoLocalClient kakao) {
        this.kakao = kakao;
    }

    @Cacheable(cacheNames = "atm:nearby", keyGenerator = "atmNearbyKeyGen", unless = "#result == null || #result.isEmpty()")
    public List<AtmDto> findNearby(double lat, double lng, Integer radiusMeters, String brand, String q,
                                   Double swLat, Double swLng, Double neLat, Double neLng) {
        final int effectiveRadius = (radiusMeters != null)
                ? radiusMeters
                : computeRadiusFromBounds(swLat, swLng, neLat, neLng, 1500);
        log.debug("nearby lat={}, lng={}, effectiveRadius={}, brand={}, q={}, bounds=[{},{}]~[{},{}]",
                lat, lng, effectiveRadius, brand, q, swLat, swLng, neLat, neLng);
        List<KakaoLocalSearchResponse.Document> docs = new ArrayList<>();
        for (int page = 1; page <= 3; page++) {
            KakaoLocalSearchResponse res = kakao.searchBanks(lat, lng, effectiveRadius, page, 15);
            if (res == null || res.getDocuments() == null) break;
            docs.addAll(res.getDocuments());
            if (res.getMeta() != null && Boolean.TRUE.equals(res.getMeta().getIs_end())) break;
        }

        Map<String, AtmDto> uniq = new LinkedHashMap<>();
        for (KakaoLocalSearchResponse.Document d : docs) {
            double dLat = parseOr(d.getY(), 0.0);
            double dLng = parseOr(d.getX(), 0.0);
            if (dLat == 0.0 && dLng == 0.0) continue;

            String placeName = nvl(d.getPlace_name());
            String address = !isBlank(d.getRoad_address_name()) ? d.getRoad_address_name() : nvl(d.getAddress_name());

            String inferredBrand = inferBrand(placeName, d.getCategory_name());
            AtmType type = classifyType(placeName, d.getCategory_name());

            double dist = distanceMeters(lat, lng, dLat, dLng);
            if (dist > Math.max(0, effectiveRadius)) continue;

            if (!"전체".equals(brand) && !"KB국민은행".equals(inferredBrand)) continue;

            if (!isBlank(q)) {
                String hay = (placeName + " " + address).toLowerCase(Locale.ROOT);
                if (!hay.contains(q.trim().toLowerCase(Locale.ROOT))) continue;
            }

            String id = "kakao:" + d.getId();
            uniq.putIfAbsent(id, new AtmDto(
                    id,
                    inferredBrand,
                    placeName,
                    address,
                    dLat,
                    dLng,
                    type
            ));
        }

        return uniq.values().stream()
                .map(a -> new Scored(a, distanceMeters(lat, lng, a.getLat(), a.getLng()),
                        a.getType() == AtmType.ATM ? 0 : 1))
                .sorted(Comparator
                        .comparingInt((Scored s) -> s.atmFirst)
                        .thenComparingDouble(s -> s.distanceMeters))
                .map(s -> s.atm)
                .collect(Collectors.toList());
    }

    private static class Scored {
        final AtmDto atm;
        final double distanceMeters;
        final int atmFirst;
        Scored(AtmDto atm, double distanceMeters, int atmFirst) {
            this.atm = atm; this.distanceMeters = distanceMeters; this.atmFirst = atmFirst;
        }
    }

    private static String nvl(String s) { return s == null ? "" : s; }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static double parseOr(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }

    private static String inferBrand(String placeName, String categoryName) {
        String hay = (nvl(placeName) + " " + nvl(categoryName)).toLowerCase(Locale.ROOT);
        if (hay.contains("kb") || hay.contains("국민")) return "KB국민은행";
        return "기타";
    }

    private static AtmType classifyType(String placeName, String categoryName) {
        String hay = (nvl(placeName) + " " + nvl(categoryName)).toLowerCase(Locale.ROOT);
        if (hay.matches(".*(atm|무인|자동화|cd|현금|지급기).*")) return AtmType.ATM;
        if (hay.matches(".*(지점|영업점|은행).*")) return AtmType.BRANCH;
        return AtmType.OTHER;
    }

    private static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
    private static int computeRadiusFromBounds(Double swLat, Double swLng, Double neLat, Double neLng, int defaultRadius) {
        if (swLat == null || swLng == null || neLat == null || neLng == null) return defaultRadius;
        double diagMeters = distanceMeters(swLat, swLng, neLat, neLng);
        int r = (int) Math.round(diagMeters / 2.0);
        return Math.max(300, Math.min(r, 5000)); // 300m ~ 5000m로 클램프
    }
}