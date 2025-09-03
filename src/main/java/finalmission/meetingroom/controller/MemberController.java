package finalmission.meetingroom.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finalmission.meetingroom.service.MemberService;
import finalmission.meetingroom.service.request.SignupRequest;
import finalmission.meetingroom.service.response.MemberResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> signup(@RequestBody final SignupRequest request) {
        MemberResponse memberResponse = memberService.createMember(request);

        return ResponseEntity.created(URI.create("/members" + memberResponse.memberId()))
                .body(memberResponse);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> withdraw(@PathVariable("memberId") final Long memberId) {
        memberService.deleteMember(memberId);

        return ResponseEntity.noContent()
                .build();
    }
}
