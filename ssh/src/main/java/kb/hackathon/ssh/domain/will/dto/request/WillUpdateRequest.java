package kb.hackathon.ssh.domain.will.dto.request;

import jakarta.validation.constraints.*;
import kb.hackathon.ssh.domain.will.entity.DonationType;

public record WillUpdateRequest(

        DonationType donationType,

        @Min(value = 1, message = "기부 금액은 1원 이상이어야 합니다")
        Long donationAmount,

        @DecimalMin(value = "0.1", message = "기부 비율은 0.1% 이상이어야 합니다")
        @DecimalMax(value = "100.0", message = "기부 비율은 100%를 초과할 수 없습니다")
        Double donationPercentage,

        @Size(max = 10000, message = "유언 내용은 10000자를 초과할 수 없습니다")
        String contentText,

        @Size(max = 255, message = "녹음 파일 경로는 255자를 초과할 수 없습니다")
        String audioFileUrl,

        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
        String witnessEmail,

        Long donationOrgId
) {
        public boolean hasDonationType() { return donationType != null; }
        public boolean hasDonationAmount() { return donationAmount != null; }
        public boolean hasDonationPercentage() { return donationPercentage != null; }
        public boolean hasContentText() { return contentText != null; }
        public boolean hasAudioFileUrl() { return audioFileUrl != null; }
        public boolean hasWitnessEmail() { return witnessEmail != null; }
        public boolean hasDonationOrgId() { return donationOrgId != null; }
}
