package finalmission.meetingroom.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finalmission.meetingroom.service.MeetingRoomService;
import finalmission.meetingroom.service.request.MeetingRoomCreateRequest;
import finalmission.meetingroom.service.response.MeetingRoomResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/rooms")
@RestController
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @GetMapping
    public ResponseEntity<List<MeetingRoomResponse>> getMeetingRooms() {
        List<MeetingRoomResponse> responses = meetingRoomService.getMeetingRooms();

        return ResponseEntity.ok()
                .body(responses);
    }

    @PostMapping
    public ResponseEntity<MeetingRoomResponse> createMeetingRoomReservation(
            @RequestBody final MeetingRoomCreateRequest request
    ) {
        MeetingRoomResponse response = meetingRoomService.createMeetingRoom(request);
        return ResponseEntity.created(URI.create("/rooms/" + response.meetingRoomId()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingRoom(
            @PathVariable final Long id
    ) {
        meetingRoomService.deleteMeetingRoom(id);
        return ResponseEntity.noContent()
                .build();
    }
}
