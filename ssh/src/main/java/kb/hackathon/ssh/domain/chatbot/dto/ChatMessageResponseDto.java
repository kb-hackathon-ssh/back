package kb.hackathon.ssh.domain.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatMessageResponseDto(
        String responseText,
        String actionType,
        Object actionData,
        String conversationContext
) {
}
