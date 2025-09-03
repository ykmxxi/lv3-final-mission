package finalmission.meetingroom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finalmission.meetingroom.common.exception.AlreadyInUseException;
import finalmission.meetingroom.common.exception.EntityNotFoundException;
import finalmission.meetingroom.domain.Member;
import finalmission.meetingroom.repository.MemberRepository;
import finalmission.meetingroom.service.request.SignupRequest;
import finalmission.meetingroom.service.response.MemberResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse createMember(final SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new AlreadyInUseException("이미 존재하는 이메일 입니다.");
        }

        Member member = new Member(request.name(), request.email(), request.password());
        memberRepository.save(member);
        return MemberResponse.from(member);
    }

    @Transactional
    public void deleteMember(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("존재하지 않는 사용자입니다.");
        }

        memberRepository.deleteById(memberId);
    }
}
