package kb.hackathon.ssh.domain.will.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kb.hackathon.ssh.domain.will.entity.DonationType;

import java.time.LocalDateTime;

public record WillResponse(
        Long willId,
        Long userId,
        Long  donationOrgId,
        DonationType donationType,
        Long donationAmount,
        Double donationPercentage,
        String contentText,
        String audioFileUrl,
        String witnessEmail,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}
