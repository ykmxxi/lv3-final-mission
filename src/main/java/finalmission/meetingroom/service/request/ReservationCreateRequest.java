package finalmission.meetingroom.service.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;

public record ReservationCreateRequest(
        @NotBlank
        String meetingRoomName,

        @NotBlank
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate reservationDate,

        @NotBlank
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt,

        @NotBlank
        @JsonFormat(pattern = "HH:mm")
        LocalTime endAt
) {
}
