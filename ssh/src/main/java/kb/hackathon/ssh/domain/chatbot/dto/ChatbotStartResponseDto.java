package kb.hackathon.ssh.domain.chatbot.dto;

import java.util.List;

public record ChatbotStartResponseDto(
        String greetingText,
        List<OptionDto> options
) {
}
