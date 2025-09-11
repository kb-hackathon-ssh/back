package kb.hackathon.ssh.domain.chatbot.service;

import kb.hackathon.ssh.domain.chatbot.dto.*;
import kb.hackathon.ssh.domain.chatbot.speech.SpeechService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatbotService {

    private final ChatClient chatClient;
    private final SpeechService speechService;

    public ChatbotService(ChatClient.Builder chatClientBuilder, SpeechService speechService) {
        this.chatClient = chatClientBuilder.build();
        this.speechService = speechService;
    }

    @Value("classpath:knowledge.txt")
    private Resource knowledgeResource;

    @Value("${chatbot.video.phishing-guide-url}")
    private String phishingGuideVideoUrl;

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            당신은 어르신들을 위한 디지털 금융 동반자 '마음 잇는 목소리' 서비스의 친절한 안내원입니다.
            아래 '참고 자료'를 바탕으로, 사용자의 '질문'에 대해 존댓말을 사용해서 쉽고 간결하게 설명해주세요.
            참고 자료에 없는 내용에 대해서는 아는 척하지 말고, "제가 도와드리기 어려운 질문이네요." 라고 솔직하게 답변해주세요.
            
            [참고 자료]
            {context}
            """;

    public ChatbotStartResponseDto getStartResponse() {
        String greeting = "안녕하세요, 어르신. 디지털 금융 동반자 '마음 잇는 목소리'입니다. 아래에서 원하시는 서비스를 선택하시거나, 편하게 말씀해주세요.";
        List<OptionDto> options = List.of(
                new OptionDto("주변 ATM 찾기 안내", "ATM은 어떻게 찾아요?"),
                new OptionDto("보이스피싱 진단 안내", "보이스피싱은 어떻게 확인해요?"),
                new OptionDto("유산 기부 방법 안내", "유산 기부는 어떻게 해요?")
        );
        return new ChatbotStartResponseDto(greeting, options);
    }

    public ChatMessageResponseDto processMessage(ChatMessageRequestDto requestDto) {
        String userMessage = requestDto.userMessage().toLowerCase();
        String context = requestDto.conversationContext();
        log.info("사용자 질문: {}, 이전 맥락: {}", userMessage, context);

        if (isPositiveResponse(userMessage) && context != null) {
            switch (context) {
                case "AWAITING_ATM_HIGHLIGHT":
                    return createHighlightResponse("주변 ATM 찾기", "#header-atm-button");
                case "AWAITING_PHISHING_CHOICE":
                    if (userMessage.contains("영상")) {
                        return createVideoResponse(phishingGuideVideoUrl);
                    }
                    return createHighlightResponse("보이스피싱 진단", "#header-phishing-button");
                case "AWAITING_SIGNUP_HIGHLIGHT":
                    return createHighlightResponse("회원가입", "#header-signup-button");
            }
        }

        if (isAtmGuideRequest(userMessage)) {
            return createAtmGuideInitialResponse();
        }
        if (isPhishingGuideRequest(userMessage)) {
            return createPhishingGuideInitialResponse();
        }
        if (isSignupGuideRequest(userMessage)) {
            return createSignupGuideInitialResponse();
        }
        if (isFontSizeGuideRequest(userMessage)) {
            return createFontSizeGuideResponse();
        }

        return createGeneralResponse(userMessage);
    }

    public SpeechResponseDto processSpeechMessage(SpeechRequestDto requestDto) throws IOException {
        String userMessageText = speechService.convertSpeechToText(requestDto.audioFile());

        ChatMessageRequestDto chatMessageRequestDto = new ChatMessageRequestDto(userMessageText, requestDto.conversationContext());
        ChatMessageResponseDto chatTextResponse = processMessage(chatMessageRequestDto);

        byte[] chatbotAudio = speechService.convertTextToSpeech(chatTextResponse.responseText());

        return SpeechResponseDto.builder()
                .userMessageText(userMessageText)
                .chatbotMessageText(chatTextResponse.responseText())
                .chatbotAudio(chatbotAudio)
                .actionType(chatTextResponse.actionType())
                .actionData(chatTextResponse.actionData())
                .conversationContext(chatTextResponse.conversationContext())
                .build();
    }

    private boolean isPositiveResponse(String message) {
        return message.contains("네")
                || message.contains("응")
                || message.contains("알려")
                || message.contains("보여")
                || message.contains("그래")
                || message.contains("영상");
    }

    private boolean isAtmGuideRequest(String message) {
        return message.contains("atm") || message.contains("돈 뽑");
    }

    private boolean isPhishingGuideRequest(String message) {
        return message.contains("보이스피싱") || message.contains("사기");
    }

    private boolean isSignupGuideRequest(String message) {
        return message.contains("회원가입") || message.contains("서비스 어떻게 써");
    }

    private boolean isFontSizeGuideRequest(String message) {
        return message.contains("글자가 잘 안 보여") || message.contains("글씨 좀 키워줘");
    }

    private ChatMessageResponseDto createAtmGuideInitialResponse() {
        String responseText = "네, 어르신. 돈을 찾으셔야 하는군요. 화면 맨 위쪽을 보시면 '주변 ATM 찾기' 라고 쓰인 버튼이 항상 준비되어 있습니다. 그 버튼을 누르시면, 지금 계신 곳에서 가장 가까운 ATM 기기를 바로 찾아드립니다. 제가 버튼 위치를 알려드릴까요?";
        return new ChatMessageResponseDto(responseText, "SPEAK", null, "AWAITING_ATM_HIGHLIGHT");
    }

    private ChatMessageResponseDto createPhishingGuideInitialResponse() {
        String responseText = "많이 놀라셨겠어요, 어르신. 요즘 나쁜 사람들이 많아서 항상 조심해야 합니다. 화면 맨 위쪽을 보시면 '보이스피싱 진단'이라고 쓰인 버튼이 항상 준비되어 있습니다. 그 버튼을 누르시면, 의심스러운 전화번호나 계좌번호가 안전한지 바로 확인하실 수 있습니다. 제가 버튼 위치를 알려드릴까요, 아니면 영상으로 대처 방법을 먼저 안내해 드릴까요?";
        return new ChatMessageResponseDto(responseText, "SPEAK", null, "AWAITING_PHISHING_CHOICE");
    }

    private ChatMessageResponseDto createSignupGuideInitialResponse() {
        String responseText = "저희 '마음 잇는 목소리'에 오신 것을 환영합니다! 서비스를 이용하시려면 먼저 간단한 회원가입이 필요합니다. 화면 맨 위쪽을 보시면 '회원가입'이라고 쓰인 버튼이 준비되어 있습니다. 그 버튼을 누르시고 안내에 따라 아이디와 비밀번호, 성함, 그리고 이메일 주소만 입력해주시면 바로 저희 서비스를 이용하실 수 있습니다. 제가 버튼 위치를 알려드릴까요?";
        return new ChatMessageResponseDto(responseText, "SPEAK", null, "AWAITING_SIGNUP_HIGHLIGHT");
    }

    private ChatMessageResponseDto createFontSizeGuideResponse() {
        String responseText = "그러셨군요, 어르신. 화면 오른쪽 위에 보시면 돋보기 모양과 함께 '+' 라고 쓰인 버튼이 있습니다. 그 버튼을 누르시면 글씨를 더 크고 편안하게 보실 수 있습니다.";
        return new ChatMessageResponseDto(responseText, "SPEAK", null, null);
    }

    private ChatMessageResponseDto createHighlightResponse(String featureName, String elementId) {
        String responseText = "네, 화면 맨 위쪽에 있는 '" + featureName + "' 버튼을 제가 바로 강조해서 보여드릴게요.";
        Map<String, String> actionData = Map.of("elementId", elementId);
        return new ChatMessageResponseDto(responseText, "HIGHLIGHT_ELEMENT", actionData, null);
    }

    private ChatMessageResponseDto createVideoResponse(String videoUrl) {
        String responseText = "네, 보이스피싱 대처 방법을 영상으로 쉽고 정확하게 알려드릴게요. 잠시 화면을 봐주세요.";
        Map<String, String> actionData = Map.of("videoUrl", videoUrl);
        return new ChatMessageResponseDto(responseText, "SHOW_VIDEO", actionData, null);
    }

    private ChatMessageResponseDto createGeneralResponse(String userMessage) {
        String knowledge = loadKnowledge();
        String responseText = getAnswerFromLLM(userMessage, knowledge);

        if (userMessage.contains("검찰") || userMessage.contains("대처 방법")) {
            return new ChatMessageResponseDto(responseText, "SHOW_VIDEO", Map.of("videoUrl", phishingGuideVideoUrl), null);
        }

        return new ChatMessageResponseDto(responseText, "SPEAK", null, null);
    }

    private String loadKnowledge() {
        try {
            return knowledgeResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("knowledge.txt 파일을 읽는 중 오류가 발생했습니다.", e);
            return "서비스에 대한 기본적인 안내를 제공합니다.";
        }
    }

    private String getAnswerFromLLM(String userMessage, String context) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_PROMPT_TEMPLATE);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("context", context));
        Message userMessagePrompt = new UserMessage(userMessage);
        Prompt finalPrompt = new Prompt(List.of(systemMessage, userMessagePrompt));
        return chatClient.prompt(finalPrompt).call().content();
    }
}
