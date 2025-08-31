package kb.hackathon.ssh.global.util;

import kb.hackathon.ssh.global.dto.ApiResponse;
import org.springframework.http.HttpStatus;

public class ApiUtils {

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    public static ApiResponse<?> error(String message, HttpStatus status) {
        return ApiResponse.error(new ApiError(message, status.value()));
    }

    public static ApiResponse<?> error(ApiError error) {
        return ApiResponse.error(error);
    }


    public record ApiError(
            String message,
            int status
    ) {
    }
}
