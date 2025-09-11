package kb.hackathon.ssh.domain.phishing.service;

import kb.hackathon.ssh.domain.phishing.dto.PhishingDto;
import kb.hackathon.ssh.domain.phishing.entity.Phishing;
import kb.hackathon.ssh.domain.phishing.repository.PhishingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhishingService {

    private final PhishingRepository phishingRepository;

    @Transactional(readOnly = true)
    public PhishingDto.LookupResponse lookup(String type, String normalized) {

        long reportsL = phishingRepository.countByTypeAndNormalized(type, normalized);
        LocalDateTime last = phishingRepository.findLastReportedAt(type, normalized);

        int reports = (int) Math.min(Integer.MAX_VALUE, reportsL);
        String lastReported = (last != null) ? last.toInstant(ZoneOffset.UTC).toString() : null;

        log.info("LOOKUP type={}, normalized={}, reports={}, last={}", type, normalized, reports, lastReported);

        String risk = riskFromReports(reports);

        PhishingDto.Item item = new PhishingDto.Item(
                UUID.randomUUID().toString(),
                type,
                normalized,
                reports,
                lastReported,
                "internal",
                risk
        );

        PhishingDto.LookupResponse resp = new PhishingDto.LookupResponse();
        Map<String, String> q = Map.of(
                "type", type,
                "value", normalized,
                "normalized", normalized
        );
        Map<String, String> meta = Map.of(
                "checkedAt", Instant.now().toString(),
                "source", "internal"
        );
        resp.setQuery(q);
        resp.setMeta(meta);
        resp.getItems().add(item);
        return resp;
    }

    private String riskFromReports(int n) {
        if (n >= 10) return "high";
        if (n >= 5)  return "medium";
        return "low";
    }

    @Transactional
    public void report(String type, String normalized) {
        Phishing entity = new Phishing();
        entity.setType(type);
        entity.setValue(normalized);
        entity.setNormalized(normalized);
        entity.setStatus("REPORTED");
        entity.setReportedAt(LocalDateTime.now(ZoneOffset.UTC));

        phishingRepository.save(entity);

        log.info("REPORT SAVE type={} normalized={}", type, normalized);
    }

    @Transactional(readOnly = true)
    public List<PhishingDto.Item> recentTop5() {
        List<Map<String,Object>> rows = phishingRepository.findTop5RecentRaw();
        List<PhishingDto.Item> items = new ArrayList<>();
        for (Map<String,Object> r : rows) {
            String type = (String) r.get("type");
            String normalized = (String) r.get("normalized");
            long reports = ((Number) r.get("reports")).longValue();
            Instant last = ((Timestamp) r.get("last_reported")).toInstant();
            String risk = riskFromReports((int) reports);

            items.add(new PhishingDto.Item(
                    UUID.randomUUID().toString(),
                    type,
                    normalized,
                    (int) reports,
                    DateTimeFormatter.ISO_INSTANT.format(last),
                    "internal",
                    risk
            ));
        }
        return items;
    }
}