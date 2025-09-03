package finalmission.meetingroom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finalmission.meetingroom.common.exception.LoginFailException;
import finalmission.meetingroom.common.JwtTokenHandler;
import finalmission.meetingroom.domain.Member;
import finalmission.meetingroom.repository.MemberRepository;
import finalmission.meetingroom.service.request.LoginMember;
import finalmission.meetingroom.service.request.LoginRequest;
import finalmission.meetingroom.service.response.LoginResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenHandler jwtTokenHandler;

    public LoginResponse login(final LoginRequest request) {
        Member member = memberRepository.findByEmailAndPassword(
                request.email(), request.password()
        ).orElseThrow(() -> new LoginFailException("이메일 또는 비밀번호가 잘못 되었습니다."));

        String tokenValue = jwtTokenHandler.createToken(member);
        return new LoginResponse(tokenValue);
    }

    public LoginMember createLoginMemberByToken(final String token) {
        Long memberId = Long.parseLong(jwtTokenHandler.getMemberId(token));
        String name = jwtTokenHandler.getName(token);

        return new LoginMember(memberId, name);
    }
}
