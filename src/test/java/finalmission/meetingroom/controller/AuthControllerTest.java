package finalmission.meetingroom.controller;

import static org.hamcrest.CoreMatchers.any;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerTest {

    @DisplayName("로그인 요청을 보내면 쿠키를 만들어 반환한다.")
    @Test
    void login() {
        Map<String, String> loginParams = Map.of("email", "ykmxxi97@gmail.com", "password", "1234");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when().post("/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .cookie("token", any(String.class));
    }
}
