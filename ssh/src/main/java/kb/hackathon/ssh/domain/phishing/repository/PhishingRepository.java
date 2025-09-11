package kb.hackathon.ssh.domain.phishing.repository;

import kb.hackathon.ssh.domain.phishing.entity.Phishing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PhishingRepository extends JpaRepository<Phishing, Long> {

    List<Phishing> findByTypeAndNormalized(String type, String normalized);

    long countByTypeAndNormalized(String type, String normalized);

    @Query("select max(p.reportedAt) from Phishing p " +
            "where p.type = :type and p.normalized = :normalized")
    LocalDateTime findLastReportedAt(@Param("type") String type,
                                     @Param("normalized") String normalized);

    @Query("""
  select count(p), max(p.reportedAt)
  from Phishing p
  where p.type = :type and p.normalized = :normalized
""")
    Object[] findCountAndLastReported(@Param("type") String type,
                                      @Param("normalized") String normalized);

    @Query(value = """
        SELECT type, normalized,
               COUNT(*)       AS reports,
               MAX(reported_at) AS last_reported
          FROM phishing
         GROUP BY type, normalized
         ORDER BY last_reported DESC
         LIMIT 5
        """, nativeQuery = true)
    List<Map<String,Object>> findTop5RecentRaw();
}