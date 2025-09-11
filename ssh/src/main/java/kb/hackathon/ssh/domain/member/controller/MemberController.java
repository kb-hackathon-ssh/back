package kb.hackathon.ssh.domain.member.controller;

import jakarta.validation.Valid;
import kb.hackathon.ssh.domain.member.dto.MemberJoinRequestDto;
import kb.hackathon.ssh.domain.member.dto.MemberJoinResponseDto;
import kb.hackathon.ssh.domain.member.entity.Member;
import kb.hackathon.ssh.domain.member.exception.DuplicateMemberIdException;
import kb.hackathon.ssh.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberJoinResponseDto> join(@Valid @RequestBody MemberJoinRequestDto requestDto) {
        log.info("회원가입 요청: username={}", requestDto.username());
        Member member = memberService.join(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MemberJoinResponseDto.success("회원가입이 성공적으로 완료되었습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MemberJoinResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("유효성 검증 실패: {}", errors);
        return ResponseEntity.badRequest()
                .body(MemberJoinResponseDto.fail(errors.values().iterator().next()));
    }

    @ExceptionHandler(DuplicateMemberIdException.class)
    public ResponseEntity<MemberJoinResponseDto> handleDuplicateMemberIdException(DuplicateMemberIdException ex) {
        log.warn("아이디 중복 에러: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(MemberJoinResponseDto.fail(ex.getMessage()));
    }
}
