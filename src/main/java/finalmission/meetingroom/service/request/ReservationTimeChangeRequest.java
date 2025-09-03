package finalmission.meetingroom.service.request;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;

public record ReservationTimeChangeRequest(
        @NotBlank
        @JsonFormat(pattern = "HH:mm")
        LocalTime newStartAt,

        @NotBlank
        @JsonFormat(pattern = "HH:mm")
        LocalTime newEndAt
) {
}
