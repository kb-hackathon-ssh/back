package kb.hackathon.ssh.domain.chatbot.dto;

public record SpeechResponseDto(
        String userMessageText,
        String chatbotMessageText,
        byte[] chatbotAudio,
        String actionType,
        Object actionData,
        String conversationContext
) {
    public static SpeechResponseDtoBuilder builder() {
        return new SpeechResponseDtoBuilder();
    }

    public static class SpeechResponseDtoBuilder {
        private String userMessageText;
        private String chatbotMessageText;
        private byte[] chatbotAudio;
        private String actionType;
        private Object actionData;
        private String conversationContext;

        SpeechResponseDtoBuilder() {
        }

        public SpeechResponseDtoBuilder userMessageText(String userMessageText) {
            this.userMessageText = userMessageText;
            return this;
        }

        public SpeechResponseDtoBuilder chatbotMessageText(String chatbotMessageText) {
            this.chatbotMessageText = chatbotMessageText;
            return this;
        }

        public SpeechResponseDtoBuilder chatbotAudio(byte[] chatbotAudio) {
            this.chatbotAudio = chatbotAudio;
            return this;
        }

        public SpeechResponseDtoBuilder actionType(String actionType) {
            this.actionType = actionType;
            return this;
        }

        public SpeechResponseDtoBuilder actionData(Object actionData) {
            this.actionData = actionData;
            return this;
        }

        public SpeechResponseDtoBuilder conversationContext(String conversationContext) {
            this.conversationContext = conversationContext;
            return this;
        }

        public SpeechResponseDto build() {
            return new SpeechResponseDto(userMessageText, chatbotMessageText, chatbotAudio, actionType, actionData, conversationContext);
        }

        public String toString() {
            return "SpeechResponseDto.SpeechResponseDtoBuilder(...)";
        }
    }
}
