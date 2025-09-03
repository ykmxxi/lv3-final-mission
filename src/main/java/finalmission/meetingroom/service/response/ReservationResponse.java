package finalmission.meetingroom.service.response;

import java.time.LocalDate;
import java.time.LocalTime;

import finalmission.meetingroom.domain.Reservation;

public record ReservationResponse(
        Long reservationId,
        String memberName,
        String meetingRoomName,
        LocalDate reservationDate,
        LocalTime startAt,
        LocalTime endAt
) {

    public static ReservationResponse from(final Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getMeetingRoom().getRoomName(),
                reservation.getReservationDate(),
                reservation.getStartAt(),
                reservation.getEndAt()
        );
    }
}
