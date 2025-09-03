package finalmission.meetingroom.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemberTest {

    @DisplayName("회의실 이름이 존재하지 않으면 회의실을 생성할 수 없다.")
    @MethodSource
    @ParameterizedTest
    void createMemberWithBlankInfo(String name, String email, String password) {
        assertThatThrownBy(() -> new Member(name, email, password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> createMemberWithBlankInfo() {
        return Stream.of(
                Arguments.of(null, "email@email.com", "1234"),
                Arguments.of("", "email@email.com", "1234"),
                Arguments.of(null, null, "1234"),
                Arguments.of(null, "", "1234"),
                Arguments.of(null, "email@email.com", null),
                Arguments.of(null, "email@email.com", "")
        );
    }

}
