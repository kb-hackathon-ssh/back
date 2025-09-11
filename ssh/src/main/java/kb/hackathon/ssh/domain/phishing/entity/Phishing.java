package kb.hackathon.ssh.domain.phishing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "phishing")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Phishing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 64)
    private String value;

    @Column(nullable = false, length = 64)
    private String normalized;

    @Column(length = 20)
    private String status;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    public static class Phone {
        public static String normalize(String raw) {
            if (raw == null) return "";
            return raw.replaceAll("[^0-9]", "");
        }
        public static String validate(String digits) {
            if (digits.length() < 9 || digits.length() > 12) {
                return "전화번호 형식이 올바르지 않습니다.";
            }
            return null;
        }
    }

    public static class Account {
        public static String normalize(String raw) {
            if (raw == null) return "";
            return raw.replaceAll("[^0-9]", "");
        }
        public static String validate(String digits) {
            if (digits.length() < 8 || digits.length() > 16) {
                return "계좌번호 형식이 올바르지 않습니다.";
            }
            return null;
        }
    }
}