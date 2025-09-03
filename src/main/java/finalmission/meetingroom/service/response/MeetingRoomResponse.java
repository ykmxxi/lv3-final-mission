package finalmission.meetingroom.service.response;

import finalmission.meetingroom.domain.MeetingRoom;

public record MeetingRoomResponse(
        Long meetingRoomId,
        String roomName
) {

    public static MeetingRoomResponse from(final MeetingRoom meetingRoom) {
        return new MeetingRoomResponse(meetingRoom.getId(), meetingRoom.getRoomName());
    }
}
