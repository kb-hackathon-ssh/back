package kb.hackathon.ssh.domain.will.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "will")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Will {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "will_id")
    private Long willId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "donation_org_id", nullable = false)
    private Long donationOrgId;

    @Column(name = "content_text", columnDefinition = "LONGTEXT", nullable = false)
    private String contentText;

    @Column(name = "audio_file_url", length = 255)
    private String audioFileUrl;

    @Column(name = "witness_email", length = 100, nullable = false)
    private String witnessEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_type", nullable = false)
    private DonationType donationType;

    @Column(name = "donation_amount")
    private Long donationAmount;

    @Column(name = "donation_percentage")
    private Double donationPercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
