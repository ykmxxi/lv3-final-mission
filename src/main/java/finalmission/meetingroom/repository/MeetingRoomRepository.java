package finalmission.meetingroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import finalmission.meetingroom.domain.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    Optional<MeetingRoom> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);
}
