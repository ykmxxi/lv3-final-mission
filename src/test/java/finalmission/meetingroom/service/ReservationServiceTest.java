package finalmission.meetingroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import finalmission.meetingroom.common.exception.AlreadyInUseException;
import finalmission.meetingroom.common.exception.BusinessException;
import finalmission.meetingroom.repository.MeetingRoomRepository;
import finalmission.meetingroom.repository.MemberRepository;
import finalmission.meetingroom.repository.ReservationRepository;
import finalmission.meetingroom.service.request.LoginMember;
import finalmission.meetingroom.service.request.ReservationCreateRequest;
import finalmission.meetingroom.service.request.ReservationTimeChangeRequest;
import finalmission.meetingroom.service.response.ReservationResponse;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationServiceTest {

    @Value("${api.sendgrid.base-url}")
    private String baseUrl;

    @Value("${api.sendgrid.end-point}")
    private String endPoint;

    @Value("${api.sendgrid.api-key}")
    private String apiKey;

    private MockRestServiceServer server;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MeetingRoomRepository meetingRoomRepository;
    @Autowired
    private MemberRepository memberRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl(baseUrl);
        this.server = MockRestServiceServer.bindTo(restClientBuilder)
                .build();
        this.reservationService = new ReservationService(
                new SendGridEmailService(restClientBuilder, apiKey, endPoint),
                reservationRepository, meetingRoomRepository, memberRepository
        );
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAllInBatch();
    }

    @DisplayName("회의실을 예약한다.")
    @Test
    void reserveMeetingRoom() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime("10:00"), getTime("11:00")
        );
        LoginMember loginMember = getLoginMember();
        sendEmailByMockServer();

        // when
        ReservationResponse result = reservationService.reserveMeetingRoom(request, loginMember);

        // then
        assertThat(result).isEqualTo(new ReservationResponse(
                result.reservationId(), loginMember.name(), "임팩트룸",
                nextDate(), getTime("10:00"), getTime("11:00")
        ));
    }

    @DisplayName("과거 시간에 회의실을 예약할 수 없다.")
    @Test
    void reserveMeetingRoomInPastTime() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", LocalDate.now().minusDays(1L), getTime("10:00"), getTime("11:00")
        );
        LoginMember loginMember = getLoginMember();

        // when & then
        assertThatThrownBy(() -> reservationService.reserveMeetingRoom(request, loginMember))
                .isInstanceOf(BusinessException.class)
                .hasMessage("과거의 시간에 예약할 수 없습니다.");
    }

    @DisplayName("예약 불가능한 시간이 포함되면 예약할 수 없다.")
    @CsvSource(value = {
            "09:59,10:59",
            "21:01,22:01",
    })
    @ParameterizedTest
    void reserveMeetingRoomWithInvalidTime(String startAt, String endAt) {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime(startAt), getTime(endAt)
        );
        LoginMember loginMember = getLoginMember();

        // when & then
        assertThatThrownBy(() -> reservationService.reserveMeetingRoom(request, loginMember))
                .isInstanceOf(BusinessException.class)
                .hasMessage("예약 불가능한 시간입니다.");
    }

    @DisplayName("종료 시간이 시작 시간보다 빠르면 회의실을 예약할 수 없다.")
    @Test
    void reserveMeetingRoomWithFastEndAt() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime("11:00"), getTime("10:59")
        );
        LoginMember loginMember = getLoginMember();

        // when & then
        assertThatThrownBy(() -> reservationService.reserveMeetingRoom(request, loginMember))
                .isInstanceOf(BusinessException.class)
                .hasMessage("예약 종료 시간이 시작 시간보다 빠를 수 없습니다.");
    }

    @DisplayName("예약 시간이 겹치면 회의실을 예약할 수 없다.")
    @Test
    void reserveMeetingRoomWithOverlapsTime() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime("10:00"), getTime("11:00")
        );
        LoginMember loginMember = getLoginMember();
        sendEmailByMockServer();
        reservationService.reserveMeetingRoom(request, loginMember);

        ReservationCreateRequest overlapsRequest = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime("11:00"), getTime("12:00")
        );

        // when & then
        assertThatThrownBy(() -> reservationService.reserveMeetingRoom(overlapsRequest, loginMember))
                .isInstanceOf(AlreadyInUseException.class);
    }

    @DisplayName("회의실 예약 시간을 변경할 수 있다.")
    @Test
    void changeReservationTime() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime("10:00"), getTime("11:00")
        );
        LoginMember loginMember = getLoginMember();
        sendEmailByMockServer();
        ReservationResponse response = reservationService.reserveMeetingRoom(request, loginMember);
        ReservationTimeChangeRequest timeChangeRequest =
                new ReservationTimeChangeRequest(getTime("12:00"), getTime("13:00"));

        // when
        ReservationResponse result = reservationService.changeReservationTime(
                response.reservationId(), timeChangeRequest, loginMember
        );

        // then
        assertThat(result).isEqualTo(new ReservationResponse(
                result.reservationId(), loginMember.name(), "임팩트룸",
                nextDate(), getTime("12:00"), getTime("13:00")
        ));
    }

    @DisplayName("회의실 예약을 취소할 수 있다.")
    @Test
    void cancelReservation() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                "임팩트룸", nextDate(), getTime("10:00"), getTime("11:00")
        );
        LoginMember loginMember = getLoginMember();
        sendEmailByMockServer();
        ReservationResponse response = reservationService.reserveMeetingRoom(request, loginMember);

        // when
        reservationService.cancel(response.reservationId(), loginMember);

        // then
        assertThat(reservationRepository.findById(response.reservationId())).isEmpty();
    }

    private void sendEmailByMockServer() {
        server.expect(MockRestRequestMatchers.requestTo(baseUrl + endPoint))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess());
    }

    private LocalDate nextDate() {
        return LocalDate.now()
                .plusDays(1L);
    }

    private LocalTime getTime(final String time) {
        return LocalTime.parse(time);
    }

    private LoginMember getLoginMember() {
        return new LoginMember(1L, "유저1");
    }
}

