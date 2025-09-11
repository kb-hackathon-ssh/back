package kb.hackathon.ssh.domain.will.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DonationType {
    FIXED_AMOUNT("정액"),
    PERCENTAGE("비율");

    private final String displayName;
}
