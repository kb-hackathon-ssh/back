package kb.hackathon.ssh.domain.chatbot.service;

import kb.hackathon.ssh.domain.chatbot.dto.ChatbotStartResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatbotServiceTest {

    private final ChatbotService chatbotService = new ChatbotService();

    @Test
    @DisplayName("챗봇 시작 API는 올바른 환영 메시지와 3개의 선택지를 반환해야 한다.")
    void getStartResponse() {
        // when
        ChatbotStartResponseDto response = chatbotService.getStartResponse();

        // then
        assertNotNull(response, "응답 객체는 null이 아니어야 합니다.");
        assertEquals("안녕하세요, 어르신. 디지털 금융 동반자 '마음 잇는 목소리'입니다. 아래에서 원하시는 서비스를 선택하시거나, 편하게 말씀해주세요.",
                response.greetingText(), "환영 메시지가 기대값과 일치해야 합니다.");
        assertEquals(3, response.options().size(), "선택지 버튼의 개수는 3개여야 합니다.");
        assertEquals("주변 ATM 찾기 안내", response.options().get(0).text(), "첫 번째 버튼의 텍스트가 기대값과 일치해야 합니다.");
    }
}