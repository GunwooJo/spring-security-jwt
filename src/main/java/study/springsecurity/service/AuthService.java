package study.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.springsecurity.dto.LoginRequestDto;
import study.springsecurity.dto.SignupRequestDto;
import study.springsecurity.entity.Member;
import study.springsecurity.repository.MemberRepository;
import study.springsecurity.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public String login(LoginRequestDto loginRequestDto) {
        // 이메일로 사용자 찾기
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다.");
        }

        // JWT 토큰 생성 및 반환
        return jwtUtil.generateAccessToken(member.getUsername());
    }

    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        // 이메일 중복 확인
        if (memberRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자명 중복 확인
        if (memberRepository.findByUsername(signupRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // 회원 생성
        Member member = new Member(
            signupRequestDto.getUsername(),
            encodedPassword,
            signupRequestDto.getEmail(),
            signupRequestDto.getRole()
        );

        memberRepository.save(member);
    }
}
