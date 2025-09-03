package finalmission.meetingroom.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finalmission.meetingroom.service.ReservationService;
import finalmission.meetingroom.service.request.LoginMember;
import finalmission.meetingroom.service.request.ReservationCreateRequest;
import finalmission.meetingroom.service.request.ReservationTimeChangeRequest;
import finalmission.meetingroom.service.response.ReservationResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getMeetingRoomReservations() {
        List<ReservationResponse> responses = reservationService.getReservations();
        return ResponseEntity.ok()
                .body(responses);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyMeetingRoomReservations(final LoginMember loginMember) {
        List<ReservationResponse> responses = reservationService.getMyReservations(loginMember);
        return ResponseEntity.ok()
                .body(responses);
    }

    @GetMapping("/meeting-rooms/{roomId}/date/{reservationDate}")
    public ResponseEntity<List<ReservationResponse>> getMeetingRoomReservationsByRoomAndDate(
            @PathVariable("roomId") Long roomId,
            @PathVariable("reservationDate") @DateTimeFormat(iso = ISO.DATE) LocalDate reservationDate
    ) {
        List<ReservationResponse> responses = reservationService.getReservationsByRoomAndDate(roomId, reservationDate);
        return ResponseEntity.ok()
                .body(responses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createMeetingRoomReservation(
            @RequestBody final ReservationCreateRequest request,
            final LoginMember loginMember
    ) {
        ReservationResponse response = reservationService.reserveMeetingRoom(request, loginMember);
        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId()))
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> changeMeetingRoomReservation(
            @PathVariable final Long id,
            @RequestBody final ReservationTimeChangeRequest request,
            final LoginMember loginMember
    ) {
        ReservationResponse response = reservationService.changeReservationTime(id, request, loginMember);
        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingRoomReservation(
            @PathVariable final Long id,
            final LoginMember loginMember
    ) {
        reservationService.cancel(id, loginMember);
        return ResponseEntity.noContent()
                .build();
    }
}
