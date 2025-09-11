package kb.hackathon.ssh.domain.chatbot.speech;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SpeechService {

    String convertSpeechToText(MultipartFile audioFile) throws IOException;

    byte[] convertTextToSpeech(String text);
}
