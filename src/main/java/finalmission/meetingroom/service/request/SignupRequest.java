package finalmission.meetingroom.service.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$", message = "올바른 이메일 형식이 아닙니다.")
        String email,
        
        @NotBlank
        String password
) {
}
