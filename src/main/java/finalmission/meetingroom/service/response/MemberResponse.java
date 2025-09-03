package finalmission.meetingroom.service.response;

import finalmission.meetingroom.domain.Member;

public record MemberResponse(
        Long memberId,
        String name,
        String email
) {

    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }
}
