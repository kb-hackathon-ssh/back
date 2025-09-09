package kb.hackathon.ssh.domain.chatbot.service;

import kb.hackathon.ssh.domain.chatbot.dto.ChatMessageRequestDto;
import kb.hackathon.ssh.domain.chatbot.dto.ChatMessageResponseDto;
import kb.hackathon.ssh.domain.chatbot.dto.ChatbotStartResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    private ChatbotService chatbotService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        chatbotService = new ChatbotService(chatClientBuilder);
        ReflectionTestUtils.setField(chatbotService, "knowledgeResource", new ByteArrayResource("테스트용 지식창고 내용".getBytes()));
        ReflectionTestUtils.setField(chatbotService, "phishingGuideVideoUrl", "https://www.youtube.com/watch?v=jFdg0_b-BHc");
    }

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

    @Nested
    @DisplayName("사용자 메시지 처리 (processMessage)")
    class ProcessMessage {

        @Test
        @DisplayName("ATM 관련 첫 질문 시, 다음 대화 맥락과 함께 안내 메시지를 반환한다")
        void givenAtmFirstQuestion_whenProcessMessage_thenReturnsInitialGuide() {
            // given
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto("atm 어디야?", null);

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("SPEAK", response.actionType());
            assertEquals("AWAITING_ATM_HIGHLIGHT", response.conversationContext());
            assertTrue(response.responseText().contains("제가 버튼 위치를 알려드릴까요?"));
        }

        @Test
        @DisplayName("ATM 안내 후속 질문 시, 버튼 강조(HIGHLIGHT) 액션을 반환한다")
        void givenAtmFollowUpQuestion_whenProcessMessage_thenReturnsHighlightAction() {
            // given
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto("네 알려주세요", "AWAITING_ATM_HIGHLIGHT");

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("HIGHLIGHT_ELEMENT", response.actionType());
            assertNull(response.conversationContext());
            assertTrue(response.responseText().contains("버튼을 제가 바로 강조해서 보여드릴게요."));
            if (response.actionData() instanceof Map<?, ?> dataMap) {
                assertEquals("#header-atm-button", dataMap.get("elementId"));
            } else {
                fail("actionData should be an instance of Map.");
            }
        }

        @Test
        @DisplayName("보이스피싱 관련 첫 질문 시, 다음 대화 맥락과 함께 안내 메시지를 반환한다")
        void givenPhishingFirstQuestion_whenProcessMessage_thenReturnsInitialGuide() {
            // given
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto("이거 사기 아니야?", null);

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("SPEAK", response.actionType());
            assertEquals("AWAITING_PHISHING_CHOICE", response.conversationContext());
            assertTrue(response.responseText().contains("버튼 위치를 알려드릴까요, 아니면 영상으로"));
        }

        @Test
        @DisplayName("보이스피싱 안내 후 '영상'으로 답변 시, 비디오(SHOW_VIDEO) 액션을 반환한다")
        void givenPhishingFollowUpWithVideo_whenProcessMessage_thenReturnsVideoAction() {
            // given
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto("영상으로 보여줘", "AWAITING_PHISHING_CHOICE");

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("SHOW_VIDEO", response.actionType());
            assertNull(response.conversationContext());
            if (response.actionData() instanceof Map<?, ?> dataMap) {
                assertEquals("https://www.youtube.com/watch?v=jFdg0_b-BHc", dataMap.get("videoUrl"));
            } else {
                fail("actionData should be an instance of Map.");
            }
        }

        @Test
        @DisplayName("보이스피싱 안내 후 '버튼'으로 답변 시, 버튼 강조(HIGHLIGHT) 액션을 반환한다")
        void givenPhishingFollowUpWithButton_whenProcessMessage_thenReturnsHighlightAction() {
            // given
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto("응 위치 알려줘", "AWAITING_PHISHING_CHOICE");

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("HIGHLIGHT_ELEMENT", response.actionType());
            assertNull(response.conversationContext());
            if (response.actionData() instanceof Map<?, ?> dataMap) {
                assertEquals("#header-phishing-button", dataMap.get("elementId"));
            } else {
                fail("actionData should be an instance of Map.");
            }
        }

        @Test
        @DisplayName("일반적인 질문 시, LLM을 호출하여 답변을 생성하고 SPEAK 액션을 반환한다")
        void givenGeneralQuestion_whenProcessMessage_thenCallsLLMAndReturnSpeakAction() {
            // given
            String userMessage = "유산 기부가 뭐예요?";
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto(userMessage, null);
            String expectedLLMAnswer = "LLM이 생성한 유산 기부 설명입니다.";
            ChatClient.ChatClientRequestSpec requestSpecMock = mock(ChatClient.ChatClientRequestSpec.class);
            ChatClient.CallResponseSpec responseSpecMock = mock(ChatClient.CallResponseSpec.class);
            when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpecMock);
            when(requestSpecMock.call()).thenReturn(responseSpecMock);
            when(responseSpecMock.content()).thenReturn(expectedLLMAnswer);

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("SPEAK", response.actionType());
            assertNull(response.conversationContext());
            assertEquals(expectedLLMAnswer, response.responseText());
        }
    }
}