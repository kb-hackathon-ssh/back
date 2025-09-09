package kb.hackathon.ssh.domain.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequestDto(
        @NotBlank(message = "사용자 메시지는 비어 있을 수 없습니다.")
        String userMessage,
        String conversationContext
) {
}
