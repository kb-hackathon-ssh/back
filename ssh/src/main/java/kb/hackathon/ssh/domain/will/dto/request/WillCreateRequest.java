package kb.hackathon.ssh.domain.will.dto.request;

import jakarta.validation.constraints.*;
import kb.hackathon.ssh.domain.will.entity.DonationType;

public record WillCreateRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @NotNull(message = "기부 대상 기관 ID는 필수입니다")
        Long donationOrgId,

        @NotNull(message = "기부 유형은 필수입니다")
        DonationType donationType,

        @Min(value = 1, message = "기부 금액은 1원 이상이어야 합니다")
        Long donationAmount,

        @DecimalMin(value = "0.1", message = "기부 비율은 0.1% 이상이어야 합니다")
        @DecimalMax(value = "100.0", message = "기부 비율은 100%를 초과할 수 없습니다")
        Double donationPercentage,

        @NotBlank(message = "유언 내용은 필수입니다")
        @Size(max = 10000, message = "유언 내용은 10000자를 초과할 수 없습니다")
        String contentText,

        @Size(max = 255, message = "녹음 파일 경로는 255자를 초과할 수 없습니다")
        String audioFileUrl,

        @NotBlank(message = "증인 이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
        String witnessEmail
) {
}
