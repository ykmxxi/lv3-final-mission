package finalmission.meetingroom.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomName;

    public MeetingRoom(final Long id, final String roomName) {
        validateBlankAndNull(roomName);
        this.id = id;
        this.roomName = roomName;
    }

    public MeetingRoom(final String roomName) {
        this(null, roomName);
    }

    private void validateBlankAndNull(final String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("회의실 이름이 존재하지 않습니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeetingRoom that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
