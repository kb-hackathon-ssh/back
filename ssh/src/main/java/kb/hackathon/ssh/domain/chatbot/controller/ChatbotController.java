package kb.hackathon.ssh.domain.chatbot.controller;

import jakarta.validation.Valid;
import kb.hackathon.ssh.domain.chatbot.dto.ChatMessageRequestDto;
import kb.hackathon.ssh.domain.chatbot.dto.ChatMessageResponseDto;
import kb.hackathon.ssh.domain.chatbot.dto.ChatbotStartResponseDto;
import kb.hackathon.ssh.domain.chatbot.service.ChatbotService;
import kb.hackathon.ssh.global.dto.ApiResponse;
import kb.hackathon.ssh.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/message")
    public ApiResponse<ChatMessageResponseDto> handleMessage(@RequestBody @Valid ChatMessageRequestDto requestDto) {
        ChatMessageResponseDto responseDto = chatbotService.processMessage(requestDto);
        return ApiUtils.success(responseDto);
    }
}
