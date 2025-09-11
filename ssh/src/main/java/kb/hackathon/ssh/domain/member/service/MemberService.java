package kb.hackathon.ssh.domain.member.service;

import kb.hackathon.ssh.domain.member.dto.MemberJoinRequestDto;
import kb.hackathon.ssh.domain.member.entity.Member;
import kb.hackathon.ssh.domain.member.exception.DuplicateMemberIdException;
import kb.hackathon.ssh.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(MemberJoinRequestDto requestDto) {
        if (memberRepository.existsByUsername(requestDto.username())) {
            throw new DuplicateMemberIdException("이미 존재하는 아이디입니다: " + requestDto.username());
        }
        if (memberRepository.existsByEmail(requestDto.email())) {
            throw new DuplicateMemberIdException("이미 사용 중인 이메일입니다: " + requestDto.email());
        }

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        Member newMember = Member.builder()
                .username(requestDto.username())
                .password(encodedPassword)
                .name(requestDto.name())
                .email(requestDto.email())
                .build();

        return memberRepository.save(newMember);
    }
}
