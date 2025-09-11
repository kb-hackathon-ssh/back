package kb.hackathon.ssh.domain.chatbot.service;

import kb.hackathon.ssh.domain.chatbot.dto.*;
import kb.hackathon.ssh.domain.chatbot.speech.SpeechService;
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
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    private ChatbotService chatbotService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;
    @Mock
    private ChatClient.Builder chatClientBuilder;
    @Mock
    private SpeechService speechService;
    @Mock
    private Resource knowledgeResource;


    private final String phishingGuideVideoUrl = "https://www.youtube.com/watch?v=jFdg0_b-BHc";

    @BeforeEach
    void setUp() throws IOException {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        chatbotService = new ChatbotService(chatClientBuilder, speechService);
        ReflectionTestUtils.setField(chatbotService, "knowledgeResource", knowledgeResource);
        lenient().when(knowledgeResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn("테스트용 지식창고 내용");
        ReflectionTestUtils.setField(chatbotService, "phishingGuideVideoUrl", phishingGuideVideoUrl);
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
            verifyNoInteractions(chatClient);
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
            verifyNoInteractions(chatClient);
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
            verifyNoInteractions(chatClient);
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
                assertEquals(phishingGuideVideoUrl, dataMap.get("videoUrl"));
            } else {
                fail("actionData should be an instance of Map.");
            }
            verifyNoInteractions(chatClient);
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
            verifyNoInteractions(chatClient);
        }

        @Test
        @DisplayName("일반적인 질문 시, LLM을 호출하여 답변을 생성하고 SPEAK 액션을 반환한다")
        void givenGeneralQuestion_whenProcessMessage_thenCallsLLMAndReturnSpeakAction() {
            // given
            String userMessage = "유산 기부가 뭐예요?";
            ChatMessageRequestDto requestDto = new ChatMessageRequestDto(userMessage, null);
            String expectedLLMAnswer = "LLM이 생성한 유산 기부 설명입니다.";
            when(chatClient.prompt(any(Prompt.class)).call().content()).thenReturn(expectedLLMAnswer);

            // when
            ChatMessageResponseDto response = chatbotService.processMessage(requestDto);

            // then
            assertEquals("SPEAK", response.actionType());
            assertNull(response.conversationContext());
            assertEquals(expectedLLMAnswer, response.responseText());
            verify(chatClient, times(1)).prompt(any(Prompt.class));
        }
    }

    @Nested
    @DisplayName("음성 메시지 처리 (processSpeechMessage)")
    class ProcessSpeechMessage {

        @Mock
        private MultipartFile mockAudioFile;

        @Test
        @DisplayName("음성 ATM 요청을 처리하고 STT 텍스트와 TTS 오디오, ATM 맥락을 반환한다.")
        void givenSpeechAtmRequest_whenProcessSpeechMessage_thenReturnsSpeechResponseWithAtmContext() throws IOException {
            // given
            SpeechRequestDto speechRequestDto = new SpeechRequestDto(mockAudioFile, null);
            String sttResult = "atm 알려줘";
            byte[] ttsAudio = "네, ATM을 찾아드릴게요.".getBytes();
            when(speechService.convertSpeechToText(any(MultipartFile.class))).thenReturn(sttResult);
            when(speechService.convertTextToSpeech(anyString())).thenReturn(ttsAudio);

            // when
            SpeechResponseDto response = chatbotService.processSpeechMessage(speechRequestDto);

            // then
            assertNotNull(response);
            assertEquals(sttResult, response.userMessageText());
            assertArrayEquals(ttsAudio, response.chatbotAudio());
            assertEquals("SPEAK", response.actionType());
            assertEquals("AWAITING_ATM_HIGHLIGHT", response.conversationContext());
            verify(speechService, times(1)).convertSpeechToText(mockAudioFile);
            verify(speechService, times(1)).convertTextToSpeech(anyString());
            verifyNoInteractions(chatClient);
        }

        @Test
        @DisplayName("음성 ATM 후속 질문을 처리하고 STT 텍스트와 TTS 오디오, 강조 액션을 반환한다.")
        void givenSpeechAtmFollowUp_whenProcessSpeechMessage_thenReturnsSpeechResponseWithHighlightAction() throws IOException {
            // given
            SpeechRequestDto speechRequestDto = new SpeechRequestDto(mockAudioFile, "AWAITING_ATM_HIGHLIGHT");
            String sttResult = "네 알려주세요";
            byte[] ttsAudio = "네, 화면 맨 위쪽에 있는 '주변 ATM 찾기' 버튼을 제가 바로 강조해서 보여드릴게요.".getBytes();
            when(speechService.convertSpeechToText(any(MultipartFile.class))).thenReturn(sttResult);
            when(speechService.convertTextToSpeech(anyString())).thenReturn(ttsAudio);

            // when
            SpeechResponseDto response = chatbotService.processSpeechMessage(speechRequestDto);

            // then
            assertNotNull(response);
            assertEquals(sttResult, response.userMessageText());
            assertArrayEquals(ttsAudio, response.chatbotAudio());
            assertEquals("HIGHLIGHT_ELEMENT", response.actionType());
            assertEquals(Map.of("elementId", "#header-atm-button"), response.actionData());
            assertNull(response.conversationContext());
            verify(speechService, times(1)).convertSpeechToText(mockAudioFile);
            verify(speechService, times(1)).convertTextToSpeech(anyString());
            verifyNoInteractions(chatClient);
        }

        @Test
        @DisplayName("음성 보이스피싱 영상 요청 처리하고 STT 텍스트와 TTS 오디오, 영상 액션을 반환한다.")
        void givenSpeechPhishingVideoRequest_whenProcessSpeechMessage_thenReturnsSpeechResponseWithVideoAction() throws IOException {
            // given
            SpeechRequestDto speechRequestDto = new SpeechRequestDto(mockAudioFile, "AWAITING_PHISHING_CHOICE");
            String sttResult = "영상으로 보여줘";
            byte[] ttsAudio = "네, 보이스피싱 대처 방법을 영상으로 쉽고 정확하게 알려드릴게요.".getBytes();
            when(speechService.convertSpeechToText(any(MultipartFile.class))).thenReturn(sttResult);
            when(speechService.convertTextToSpeech(anyString())).thenReturn(ttsAudio);

            // when
            SpeechResponseDto response = chatbotService.processSpeechMessage(speechRequestDto);

            // then
            assertNotNull(response);
            assertEquals(sttResult, response.userMessageText());
            assertArrayEquals(ttsAudio, response.chatbotAudio());
            assertEquals("SHOW_VIDEO", response.actionType());
            assertEquals(Map.of("videoUrl", phishingGuideVideoUrl), response.actionData());
            assertNull(response.conversationContext());
            verify(speechService, times(1)).convertSpeechToText(mockAudioFile);
            verify(speechService, times(1)).convertTextToSpeech(anyString());
            verifyNoInteractions(chatClient);
        }

        @Test
        @DisplayName("음성 일반 질문을 처리하고 STT 텍스트와 TTS 오디오, LLM 답변을 반환한다.")
        void givenSpeechGeneralQuestion_whenProcessSpeechMessage_thenReturnsSpeechResponseWithLLMAnswer() throws IOException {
            // given
            SpeechRequestDto speechRequestDto = new SpeechRequestDto(mockAudioFile, null);
            String sttResult = "유산 기부는 어떻게 하나요?";
            String llmResponseText = "LLM이 생성한 유산 기부 답변입니다.";
            byte[] ttsAudio = llmResponseText.getBytes();
            when(speechService.convertSpeechToText(any(MultipartFile.class))).thenReturn(sttResult);
            when(speechService.convertTextToSpeech(anyString())).thenReturn(ttsAudio);
            when(chatClient.prompt(any(Prompt.class)).call().content()).thenReturn(llmResponseText);

            // when
            SpeechResponseDto response = chatbotService.processSpeechMessage(speechRequestDto);

            // then
            assertNotNull(response);
            assertEquals(sttResult, response.userMessageText());
            assertArrayEquals(ttsAudio, response.chatbotAudio());
            assertEquals("SPEAK", response.actionType());
            assertNull(response.conversationContext());
            verify(speechService, times(1)).convertSpeechToText(mockAudioFile);
            verify(speechService, times(1)).convertTextToSpeech(eq(llmResponseText));
            verify(chatClient, times(1)).prompt(any(Prompt.class));
        }
    }
}