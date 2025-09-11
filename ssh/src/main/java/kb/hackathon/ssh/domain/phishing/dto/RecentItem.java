package kb.hackathon.ssh.domain.phishing.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentItem {
    private String type;
    private String normalized;
    private long reports;
    private Instant lastReported;
}