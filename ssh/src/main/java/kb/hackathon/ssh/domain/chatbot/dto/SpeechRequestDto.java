package kb.hackathon.ssh.domain.chatbot.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public record SpeechRequestDto(
        @NotNull(message = "음성 파일은 필수입니다.") MultipartFile audioFile,
        String conversationContext
        ) {
}
