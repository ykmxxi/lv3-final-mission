package finalmission.meetingroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import finalmission.meetingroom.common.exception.AlreadyInUseException;
import finalmission.meetingroom.common.exception.EntityNotFoundException;
import finalmission.meetingroom.domain.Member;
import finalmission.meetingroom.repository.MemberRepository;
import finalmission.meetingroom.service.request.SignupRequest;
import finalmission.meetingroom.service.response.MemberResponse;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("사용자를 생성한다.")
    @Test
    void createMember() {
        SignupRequest request = new SignupRequest("포스티", "test@email.com", "1234");

        MemberResponse result = memberService.createMember(request);

        assertThat(result).isEqualTo(new MemberResponse(result.memberId(), "포스티", "test@email.com"));
    }

    @DisplayName("이미 존재하는 이메일로 사용자를 생성할 수 없다.")
    @Test
    void createMemberByDuplicatedEmail() {
        String duplicatedEmail = "test@email.com";
        memberRepository.save(new Member("구구", duplicatedEmail, "4321"));
        SignupRequest request = new SignupRequest("포스티", duplicatedEmail, "1234");

        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(AlreadyInUseException.class)
                .hasMessage("이미 존재하는 이메일 입니다.");
    }

    @DisplayName("사용자를 삭제한다.")
    @Test
    void deleteMember() {
        Member member = memberRepository.save(new Member("포스티", "test@email.com", "4321"));

        memberService.deleteMember(member.getId());

        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }

    @DisplayName("존재하지 않는 사용자를 삭제할 수 없다.")
    @Test
    void deleteMemberWithNonExistsId() {
        assertThatThrownBy(() -> memberService.deleteMember(0L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}
