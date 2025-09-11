package kb.hackathon.ssh.domain.chatbot.speech;

import com.google.cloud.speech.v1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class GcpSpeechService implements SpeechService {

    private SpeechClient speechClient;
    private TextToSpeechClient textToSpeechClient;

    @PostConstruct
    public void init() throws IOException {
        log.info("GCP Speech/Text-to-Speech 클라이언트 초기화 중...");
        speechClient = SpeechClient.create();
        textToSpeechClient = TextToSpeechClient.create();
        log.info("GCP Speech/Text-to-Speech 클라이언트 초기화 완료.");
    }

    @PreDestroy
    public void destroy() {
        if (speechClient != null) {
            speechClient.close();
            log.info("GCP SpeechClient 종료.");
        }
        if (textToSpeechClient != null) {
            textToSpeechClient.close();
            log.info("GCP TextToSpeechClient 종료.");
        }
    }

    @Override
    public String convertSpeechToText(MultipartFile audioFile) throws IOException {
        ByteString audioBytes = ByteString.copyFrom(audioFile.getBytes());

        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("ko-KR")
                .addSpeechContexts(SpeechContext.newBuilder().addPhrases("ATM").addPhrases("보이스피싱").addPhrases("회원가입"))
                .build();

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        RecognizeResponse response = speechClient.recognize(config, audio);
        StringBuilder transcript = new StringBuilder();
        for (SpeechRecognitionResult result : response.getResultsList()) {
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            transcript.append(alternative.getTranscript());
        }
        log.info("STT 반환 결과: {}", transcript);
        return transcript.toString();
    }

    @Override
    public byte[] convertTextToSpeech(String text) {
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("ko-KR")
                .setName("ko-KR-Wavenet-A")
                .setSsmlGender(SsmlVoiceGender.FEMALE)
                .build();

        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();

        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

        log.info("TTS 변환 완료. 바이트 크기: {}", response.getAudioContent().size());
        return response.getAudioContent().toByteArray();
    }
}
