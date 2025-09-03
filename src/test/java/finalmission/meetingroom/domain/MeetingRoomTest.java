package finalmission.meetingroom.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MeetingRoomTest {

    @DisplayName("회의실 이름이 존재하지 않으면 회의실을 생성할 수 없다.")
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @ParameterizedTest
    void createMeetingRoomWithBlankName(String invalidRoomName) {
        assertThatThrownBy(() -> new MeetingRoom(invalidRoomName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
