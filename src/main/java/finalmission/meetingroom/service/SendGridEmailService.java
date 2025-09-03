package finalmission.meetingroom.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import finalmission.meetingroom.common.exception.SendGridApiException;
import finalmission.meetingroom.service.request.sendgrid.EmailSendingRequest;

@Service
public class SendGridEmailService {

    private final RestClient restClient;
    private final String apiKey;
    private final String endPoint;

    public SendGridEmailService(
            final RestClient.Builder builder,
            @Value("${api.sendgrid.api-key}") final String apiKey,
            @Value("${api.sendgrid.end-point}") final String endPoint
    ) {
        this.restClient = builder.build();
        this.apiKey = apiKey;
        this.endPoint = endPoint;
    }

    public void sendEmail(final EmailSendingRequest request) {
        String authorization = "Bearer " + apiKey;

        try {
            restClient.post()
                    .uri(endPoint)
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new SendGridApiException("SendGrid Email Api Fails", e);
        }
    }
}
