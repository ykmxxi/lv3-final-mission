package finalmission.meetingroom.service.request;

import jakarta.validation.constraints.NotBlank;

public record MeetingRoomCreateRequest(
        @NotBlank
        String roomName
) {
}
