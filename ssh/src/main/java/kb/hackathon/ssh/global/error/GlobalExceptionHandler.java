package kb.hackathon.ssh.global.error;

import kb.hackathon.ssh.global.dto.ApiResponse;
import kb.hackathon.ssh.global.error.F.ErrorCode;
import kb.hackathon.ssh.global.error.exception.BusinessException;
import kb.hackathon.ssh.global.util.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid를 이용한 유효성 검증에서 에러가 발생했을 때 처리하는 핸들러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiUtils.error(errorMessage, HttpStatus.BAD_REQUEST));
    }

    /**
     * 우리가 정의한 비즈니스 로직 상의 예외를 처리하는 핸들러
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiUtils.error(new ApiUtils.ApiError(e.getErrorCode().getMessage(), e.getErrorCode().getStatus().value())));
    }

    /**
     * 위에 정의되지 않은 모든 예외를 처리하는 핸들러
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("handleException", e);
        ErrorCode internalServerError = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(internalServerError.getStatus())
                .body(ApiUtils.error(new ApiUtils.ApiError(internalServerError.getMessage(), internalServerError.getStatus().value())));
    }
}
