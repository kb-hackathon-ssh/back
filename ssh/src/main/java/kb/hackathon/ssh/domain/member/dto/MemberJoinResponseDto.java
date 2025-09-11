package kb.hackathon.ssh.domain.member.dto;

public record MemberJoinResponseDto(
        boolean success,
        String message
) {
    public static MemberJoinResponseDto success(String message) {
        return new MemberJoinResponseDto(true, message);
    }

    public static MemberJoinResponseDto fail(String message) {
        return new MemberJoinResponseDto(false, message);
    }
}
