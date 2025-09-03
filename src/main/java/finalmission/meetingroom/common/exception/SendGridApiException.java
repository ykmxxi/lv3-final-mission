package finalmission.meetingroom.common.exception;

public class SendGridApiException extends RuntimeException {

    public SendGridApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
