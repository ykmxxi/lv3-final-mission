package finalmission.meetingroom.service.request.sendgrid;

import java.util.List;

public record Personalization(
        List<ToEmail> to,
        String subject
) {
}
