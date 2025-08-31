package kb.hackathon.ssh.global.dto;

import kb.hackathon.ssh.global.util.ApiUtils.ApiError;

public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error
) {
    // 성공 응답을 만드는 정적 팩토리 메소드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 실패 응답을 만드는 정적 팩토리 메소드
    public static ApiResponse<Void> error(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }
}
