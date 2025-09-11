package kb.hackathon.ssh.domain.chatbot.controller;

import jakarta.validation.Valid;
import kb.hackathon.ssh.domain.chatbot.dto.*;
import kb.hackathon.ssh.domain.chatbot.service.ChatbotService;
import kb.hackathon.ssh.global.dto.ApiResponse;
import kb.hackathon.ssh.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
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

    @PostMapping(value = "/speech", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpeechResponseDto> handleSpeech(
            @RequestPart("audioFile") @Valid MultipartFile audioFile,
            @RequestPart(value = "conversationContext", required = false) String conversationContext
    ) throws IOException {
        log.info("Speech API 호출됨 - Context: {}", conversationContext);
        SpeechRequestDto requestDto = new SpeechRequestDto(audioFile, conversationContext);
        SpeechResponseDto responseDto = chatbotService.processSpeechMessage(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
