package kb.hackathon.ssh.global.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig{
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory cf) {
        var serializer = new GenericJackson2JsonRedisSerializer();
        var base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofMinutes(3))
                .prefixCacheNameWith("kb:");
        Map<String, RedisCacheConfiguration> caches = new HashMap<>();
        caches.put("atm:nearby", base.entryTtl(Duration.ofMinutes(3)));
        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(caches)
                .build();
    }

    @Bean(name = "atmNearbyKeyGen")
    public KeyGenerator atmNearbyKeyGen() {
        return (target, method, params) -> {
            double lat = (double) params[0];
            double lng = (double) params[1];
            Integer radiusMeters = (Integer) params[2];
            String brand = (String) params[3];
            String q = (String) params[4];
            Double swLat = (Double) params[5];
            Double swLng = (Double) params[6];
            Double neLat = (Double) params[7];
            Double neLng = (Double) params[8];

            int effectiveRadius = (radiusMeters != null) ? radiusMeters
                    : computeRadiusFromBounds(swLat, swLng, neLat, neLng, 1500);

            double qLat = Math.round(lat * 1000.0) / 1000.0;
            double qLng = Math.round(lng * 1000.0) / 1000.0;
            int rBand = ((effectiveRadius + 99) / 100) * 100;

            String b = brand == null ? "전체" : brand.trim();
            String qq = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);

            return String.format("%.3f,%.3f:r%d:b%s:q%s", qLat, qLng, rBand, b, qq);        };
    }

    private static int computeRadiusFromBounds(Double swLat, Double swLng, Double neLat, Double neLng, int defaultRadius) {
        if (swLat == null || swLng == null || neLat == null || neLng == null) return defaultRadius;
        final double R = 6371000d;
        double dLat = Math.toRadians(neLat - swLat);
        double dLon = Math.toRadians(neLng - swLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(swLat)) * Math.cos(Math.toRadians(neLat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double diag = 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        int r = (int) Math.round(diag / 2.0);
        return Math.max(300, Math.min(r, 5000));
    }

}

