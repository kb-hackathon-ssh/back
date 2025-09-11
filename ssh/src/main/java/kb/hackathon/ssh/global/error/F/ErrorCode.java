package kb.hackathon.ssh.global.error.F;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 회원입니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "U002", "이미 가입된 이메일입니다."),
    LOGIN_ID_DUPLICATION(HttpStatus.CONFLICT, "U003", "이미 사용 중인 아이디입니다."),
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST, "U004", "아이디 또는 비밀번호가 일치하지 않습니다."),

    // Phishing
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "조회된 계좌 정보가 없습니다."),

    // Will
    WILL_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "존재하지 않는 유언장입니다."),
    WILL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "W002", "유언장에 대한 접근 권한이 없습니다."),
    WILL_STT_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "W003", "음성을 텍스트로 변환하는 중 오류가 발생했습니다."),
    WILL_AUDIO_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "W004", "음성 파일 업로드 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
