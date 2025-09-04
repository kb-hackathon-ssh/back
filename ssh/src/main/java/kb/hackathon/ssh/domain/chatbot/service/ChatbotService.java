package kb.hackathon.ssh.domain.chatbot.service;

import kb.hackathon.ssh.domain.chatbot.dto.ChatbotStartResponseDto;
import kb.hackathon.ssh.domain.chatbot.dto.OptionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotService {

    private static final String PROMPT_TEMPLATE = """
            당신은 어르신들을 위한 디지털 금융 동반자 '마음 잇는 목소리' 서비스의 친절한 안내원입니다.
            아래 '참고 자료'를 바탕으로, 사용자의 '질문'에 대해 존댓말을 사용해서 쉽고 간결하게 설명해주세요.
            참고 자료에 없는 내용에 대해서는 아는 척하지 말고, "제가 도와드리기 어려운 질문이네요." 라고 솔직하게 답변해주세요.
            
            [참고 자료]
            %s
            
            [질문]
            %s
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

    private String getAnswerFromLLM(String userMessage, String knowledge) {
        String finalPrompt = String.format(PROMPT_TEMPLATE, knowledge, userMessage);

        // ... finalPrompt를 LLM API로 보내는 로직 ...
        return "LLM의 답변";
    }
}
