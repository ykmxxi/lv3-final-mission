package finalmission.meetingroom.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationControllerTest {

    @DisplayName("회의실 예약 생성 요청을 보낸다.")
    @Test
    void postMeetingRoomReservation() {
        Map<String, Object> reservationParams = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "10:00",
                "endAt", "11:00"
        );

        RestAssured.given().log().all()
                .cookie("token", getUserTokenValue("ykmxxi97@gmail.com", "1234"))
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("모든 회의실 예약 현황을 조회한다.")
    @Test
    void getMeetingRoomReservations() {
        String userTokenValue = getUserTokenValue("ykmxxi97@gmail.com", "1234");
        Map<String, Object> reservationParams = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "10:00",
                "endAt", "11:00"
        );
        Map<String, Object> reservationParams2 = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "11:01",
                "endAt", "12:00"
        );

        RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then();

        RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams2)
                .when().post("/reservations")
                .then();

        RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @DisplayName("나의 회의실 예약 현황을 조회한다.")
    @Test
    void getMyMeetingRoomReservations() {
        Map<String, Object> reservationParams = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "10:00",
                "endAt", "11:00"
        );
        Map<String, Object> reservationParams2 = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "12:00",
                "endAt", "13:00"
        );

        String myToken = getUserTokenValue("ykmxxi97@gmail.com", "1234");
        RestAssured.given().log().all()
                .cookie("token", myToken)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then();
        RestAssured.given().log().all()
                .cookie("token", getUserTokenValue("2222@email.com", "1234"))
                .contentType(ContentType.JSON)
                .body(reservationParams2)
                .when().post("/reservations")
                .then();

        RestAssured.given().log().all()
                .cookie("token", myToken)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().get("/reservations/my")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("회의실 예약 시간 수정 요청을 보낸다.")
    @Test
    void patchMyMeetingRoomReservation() {
        Map<String, Object> reservationParams = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "10:00",
                "endAt", "11:00"
        );

        String userTokenValue = getUserTokenValue("ykmxxi97@gmail.com", "1234");
        int reservationId = (int) RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().extract().path("reservationId");

        Map<String, Object> changeReservationParams = Map.of(
                "newStartAt", "12:00",
                "newEndAt", "13:00"
        );
        RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .contentType(ContentType.JSON)
                .body(changeReservationParams)
                .when().patch("/reservations/" + reservationId)
                .then().log().all()
                .statusCode(200)
                .body("startAt", response -> equalTo("12:00:00"))
                .body("endAt", response -> equalTo("13:00:00"));
    }

    @DisplayName("회의실 예약 삭제 요청을 보낸다.")
    @Test
    void deleteMyMeetingRoomReservation() {
        Map<String, Object> reservationParams = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", LocalDate.now().plusDays(1L),
                "startAt", "10:00",
                "endAt", "11:00"
        );

        String userTokenValue = getUserTokenValue("ykmxxi97@gmail.com", "1234");
        int reservationId = RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().extract().path("reservationId");

        RestAssured.given().log().all()
                .cookie("token", userTokenValue)
                .when().delete("/reservations/" + reservationId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("지정한 날짜의 회의실 예약 현황을 조회한다.")
    @Test
    void getMeetingRoomReservationsByRoomIdAndDate() {
        LocalDate reservationDate = LocalDate.now().plusDays(1L);

        Map<String, Object> reservationParams = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", reservationDate,
                "startAt", "10:00",
                "endAt", "11:00"
        );
        Map<String, Object> reservationParams2 = Map.of(
                "meetingRoomName", "임팩트룸",
                "reservationDate", reservationDate,
                "startAt", "12:00",
                "endAt", "13:00"
        );

        String myToken = getUserTokenValue("ykmxxi97@gmail.com", "1234");
        RestAssured.given().log().all()
                .cookie("token", myToken)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then();
        RestAssured.given().log().all()
                .cookie("token", myToken)
                .contentType(ContentType.JSON)
                .body(reservationParams2)
                .when().post("/reservations")
                .then();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/reservations/meeting-rooms/" + "1" + "/date/" + reservationDate)
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    private String getUserTokenValue(final String email, final String password) {
        Map<String, String> loginParams = Map.of("email", email, "password", password);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when().post("/login")
                .then()
                .extract().cookie("token");
    }
}
