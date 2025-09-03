package finalmission.meetingroom.service.request.sendgrid;

import java.util.List;

public record EmailSendingRequest(
        List<Personalization> personalizations,
        FromEmail from,
        List<EmailContent> content
) {
}
