package finalmission.meetingroom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReservationTest {

    @DisplayName("총 예약 시간이 한 시간 초과인지 알려준다.")
    @CsvSource(value = {
            "10:00,11:01,true",
            "10:00,11:00,false"
    })
    @ParameterizedTest
    void isOverOneHour(LocalTime startAt, LocalTime endAt, boolean expected) {
        MeetingRoom meetingRoom = new MeetingRoom("임팩트룸");
        Member member = new Member("포스티", "test@email.com", "1234");
        Reservation reservation = new Reservation(meetingRoom, member, LocalDate.now(), startAt, endAt);

        assertThat(reservation.isOverOneHour()).isEqualTo(expected);
    }
}
