package finalmission.meetingroom.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import finalmission.meetingroom.domain.MeetingRoom;
import finalmission.meetingroom.domain.Member;
import finalmission.meetingroom.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Override
    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.meetingRoom
            JOIN FETCH r.member
            """)
    List<Reservation> findAll();

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.meetingRoom
            JOIN FETCH r.member
            WHERE r.member = :member
            """)
    List<Reservation> findByMember(Member member);

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.meetingRoom
            JOIN FETCH r.member
            WHERE r.id = :id
                AND r.member = :member
            """)
    Optional<Reservation> findByIdAndMember(Long id, Member member);

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN FETCH r.meetingRoom
            JOIN FETCH r.member
            WHERE r.meetingRoom.id = :meetingRoomId
                AND r.reservationDate = :reservationDate
            """)
    List<Reservation> findByMeetingRoomIdAndReservationDate(Long meetingRoomId, LocalDate reservationDate);

    boolean existsByMeetingRoomAndReservationDateAndStartAtBetween(
            MeetingRoom meetingRoom,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    );

    boolean existsByMeetingRoomAndReservationDateAndEndAtBetween(
            MeetingRoom meetingRoom,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    );

    boolean existsByIdAndMember(Long id, Member member);

    void deleteByIdAndMember(Long id, Member member);
}
