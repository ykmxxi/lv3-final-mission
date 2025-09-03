package finalmission.meetingroom.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finalmission.meetingroom.common.exception.AlreadyInUseException;
import finalmission.meetingroom.common.exception.EntityNotFoundException;
import finalmission.meetingroom.domain.MeetingRoom;
import finalmission.meetingroom.repository.MeetingRoomRepository;
import finalmission.meetingroom.service.request.MeetingRoomCreateRequest;
import finalmission.meetingroom.service.response.MeetingRoomResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    @Transactional
    public MeetingRoomResponse createMeetingRoom(final MeetingRoomCreateRequest request) {
        if (meetingRoomRepository.existsByRoomName(request.roomName())) {
            throw new AlreadyInUseException("이미 존재하는 회의실 입니다.");
        }

        MeetingRoom meetingRoom = new MeetingRoom(request.roomName());
        meetingRoomRepository.save(meetingRoom);

        return MeetingRoomResponse.from(meetingRoom);
    }

    @Transactional(readOnly = true)
    public List<MeetingRoomResponse> getMeetingRooms() {
        return meetingRoomRepository.findAll()
                .stream()
                .map(MeetingRoomResponse::from)
                .toList();
    }

    @Transactional
    public void deleteMeetingRoom(final Long meetingRoomId) {
        if (!meetingRoomRepository.existsById(meetingRoomId)) {
            throw new EntityNotFoundException("존재하지 않는 회의실 입니다.");
        }
        meetingRoomRepository.deleteById(meetingRoomId);
    }
}
