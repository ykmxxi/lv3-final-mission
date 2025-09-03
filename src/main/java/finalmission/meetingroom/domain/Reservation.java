package finalmission.meetingroom.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Reservation {

    private static final long ONE_HOUR_TO_MINUTES = 60L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_id", nullable = false)
    private MeetingRoom meetingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalTime startAt;

    @Column(nullable = false)
    private LocalTime endAt;

    public Reservation(
            final Long id,
            final MeetingRoom meetingRoom,
            final Member member,
            final LocalDate reservationDate,
            final LocalTime startAt,
            final LocalTime endAt
    ) {
        this.id = id;
        this.meetingRoom = meetingRoom;
        this.member = member;
        this.reservationDate = reservationDate;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Reservation(
            final MeetingRoom meetingRoom,
            final Member member,
            final LocalDate reservationDate,
            final LocalTime startAt,
            final LocalTime endAt
    ) {
        this(null, meetingRoom, member, reservationDate, startAt, endAt);
    }

    public void changeReservationTime(
            final LocalTime newStartAt,
            final LocalTime newEndAt
    ) {
        this.startAt = newStartAt;
        this.endAt = newEndAt;
    }

    public boolean isOverOneHour() {
        long totalReservationTime = ChronoUnit.MINUTES.between(startAt, endAt);
        return totalReservationTime > ONE_HOUR_TO_MINUTES;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
