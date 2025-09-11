package kb.hackathon.ssh.domain.will.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DonationCategory {

    DOMESTIC_CORPORATION("국내 법인"),
    RELIGIOUS_FOUNDATION("창학재단/종교"),
    SOCIAL_WELFARE("사회복지/사업일"),
    INTERNATIONAL_RELIEF("국제구호"),
    ENVIRONMENTAL_ANIMAL("환경/동물보호"),
    CULTURE_ARTS("문화/예술");

    private final String displayName;
}
