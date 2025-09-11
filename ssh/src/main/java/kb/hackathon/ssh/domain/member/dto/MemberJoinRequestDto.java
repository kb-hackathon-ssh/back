package kb.hackathon.ssh.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberJoinRequestDto(
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Size(min = 3, max = 20, message = "아이디는 3~20자 이내로 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문 대소문자, 숫자만 가능합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내로 입력해주세요.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()])[a-zA-Z\\d!@#$%^&*()]+$",
                message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(min = 2, max = 30, message = "이름은 2~30자 이내로 입력해주세요.")
        String name,

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
        @Email(message = "유효한 이메일 주소 형식이 아닙니다.")
        String email
) {
}
