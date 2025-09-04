package kb.hackathon.ssh.domain.chatbot.controller;

import kb.hackathon.ssh.domain.chatbot.dto.ChatbotStartResponseDto;
import kb.hackathon.ssh.domain.chatbot.service.ChatbotService;
import kb.hackathon.ssh.global.dto.ApiResponse;
import kb.hackathon.ssh.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @GetMapping("/start")
    public ApiResponse<ChatbotStartResponseDto> startChat() {
        ChatbotStartResponseDto responseDto = chatbotService.getStartResponse();
        return ApiUtils.success(responseDto);
    }
}
