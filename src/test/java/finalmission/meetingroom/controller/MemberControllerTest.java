package finalmission.meetingroom.controller;

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
class MemberControllerTest {

    @DisplayName("회원가입 요청을 보낸다.")
    @Test
    void postMember() {
        Map<String, String> signupParams = Map.of(
                "name", "포스티",
                "email", "test@email.com",
                "password", "1234"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(signupParams)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("이메일이 중복되면 회원가입이 실패한다.")
    @Test
    void postMemberWithDuplicationEmail() {
        Map<String, String> signupParams1 = Map.of(
                "name", "포스티",
                "email", "test@email.com",
                "password", "1234"
        );
        Map<String, String> signupParams2 = Map.of(
                "name", "구구",
                "email", "test@email.com",
                "password", "4321"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(signupParams1)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(signupParams2)
                .when().post("/members")
                .then().log().all()
                .statusCode(409);
    }

    @DisplayName("회원탈퇴 요청을 보낸다.")
    @Test
    void deleteMember() {
        Map<String, String> signupParams = Map.of(
                "name", "포스티",
                "email", "test@email.com",
                "password", "1234"
        );
        int memberId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signupParams)
                .when().post("/members")
                .then().extract().path("memberId");

        RestAssured.given().log().all()
                .when().delete("/members/" + memberId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("회원탈퇴 요청을 보낸다.")
    @Test
    void deleteMemberWithNonExistsMemberId() {
        RestAssured.given().log().all()
                .when().delete("/members/" + 0)
                .then().log().all()
                .statusCode(404);
    }
}
