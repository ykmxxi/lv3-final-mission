package finalmission.meetingroom.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finalmission.meetingroom.common.exception.AlreadyInUseException;
import finalmission.meetingroom.common.exception.BusinessException;
import finalmission.meetingroom.common.exception.EntityNotFoundException;
import finalmission.meetingroom.domain.MeetingRoom;
import finalmission.meetingroom.domain.Member;
import finalmission.meetingroom.domain.Reservation;
import finalmission.meetingroom.repository.MeetingRoomRepository;
import finalmission.meetingroom.repository.MemberRepository;
import finalmission.meetingroom.repository.ReservationRepository;
import finalmission.meetingroom.service.request.LoginMember;
import finalmission.meetingroom.service.request.ReservationCreateRequest;
import finalmission.meetingroom.service.request.ReservationTimeChangeRequest;
import finalmission.meetingroom.service.request.sendgrid.EmailContent;
import finalmission.meetingroom.service.request.sendgrid.EmailSendingRequest;
import finalmission.meetingroom.service.request.sendgrid.FromEmail;
import finalmission.meetingroom.service.request.sendgrid.Personalization;
import finalmission.meetingroom.service.request.sendgrid.ToEmail;
import finalmission.meetingroom.service.response.ReservationResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationService {

    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(22, 0);

    private final SendGridEmailService sendGridEmailService;
    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MemberRepository memberRepository;

    @Value("${api.sendgrid.from-email}")
    private String fromEmail;

    @Transactional
    public ReservationResponse reserveMeetingRoom(
            final ReservationCreateRequest request,
            final LoginMember loginMember
    ) {
        LocalDate reservationDate = request.reservationDate();
        LocalTime startAt = request.startAt();
        LocalTime endAt = request.endAt();
        validateReservationDateTime(reservationDate, startAt, endAt);

        MeetingRoom meetingRoom = getMeetingRoom(request.meetingRoomName());
        if (isAlreadyReserved(meetingRoom, reservationDate, startAt, endAt)) {
            throw new AlreadyInUseException("해당 시간에 예약이 이미 존재합니다.");
        }
        Member member = getMember(loginMember);

        Reservation reservation = new Reservation(
                meetingRoom, member, reservationDate, startAt, endAt
        );
        validateTotalReservationTime(reservation);
        reservationRepository.save(reservation);
        sendReservationCompleteEmail(loginMember);

        return ReservationResponse.from(reservation);
    }

    private void validateReservationDateTime(
            final LocalDate reservationDate,
            final LocalTime startAt,
            final LocalTime endAt
    ) {
        if (isPastTime(reservationDate, startAt)) {
            throw new BusinessException("과거의 시간에 예약할 수 없습니다.");
        }
        if (isInvalidTime(startAt) || isInvalidTime(endAt)) {
            throw new BusinessException("예약 불가능한 시간입니다.");
        }
        if (isEndAtBeforeStartAt(startAt, endAt)) {
            throw new BusinessException("예약 종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }
    }

    private boolean isPastTime(final LocalDate reservationDate, final LocalTime startAt) {
        LocalDateTime reservationStartDateTime = LocalDateTime.of(reservationDate, startAt);
        return LocalDateTime.now().isAfter(reservationStartDateTime);
    }

    private boolean isInvalidTime(final LocalTime reservationTIme) {
        return START_TIME.isAfter(reservationTIme) || END_TIME.isBefore(reservationTIme);
    }

    private boolean isAlreadyReserved(
            final MeetingRoom meetingRoom,
            final LocalDate reservationDate,
            final LocalTime startAt,
            final LocalTime endAt
    ) {
        return !reservationRepository.existsByMeetingRoomAndReservationDateAndStartAtBetween(
                meetingRoom, reservationDate, startAt, endAt) &&
               reservationRepository.existsByMeetingRoomAndReservationDateAndEndAtBetween(
                       meetingRoom, reservationDate, startAt, endAt);
    }

    private boolean isEndAtBeforeStartAt(final LocalTime startAt, final LocalTime endAt) {
        return endAt.isBefore(startAt);
    }

    private void validateTotalReservationTime(final Reservation reservation) {
        if (reservation.isOverOneHour()) {
            throw new BusinessException("총 예약 시간은 한 시간을 초과할 수 없습니다.");
        }
    }

    private void sendReservationCompleteEmail(final LoginMember loginMember) {
        Member member = getMember(loginMember);

        sendGridEmailService.sendEmail(new EmailSendingRequest(
                List.of(new Personalization(List.of(new ToEmail(member.getEmail())), "회의실 예약 완료.")),
                new FromEmail(fromEmail),
                List.of(new EmailContent("text/plain", "회의실 예약이 완료되었습니다."))
        ));
    }

    public List<ReservationResponse> getReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> getMyReservations(final LoginMember loginMember) {
        Member member = getMember(loginMember);
        return reservationRepository.findByMember(member)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse changeReservationTime(
            final Long reservationId,
            final ReservationTimeChangeRequest request,
            final LoginMember loginMember
    ) {
        Member member = getMember(loginMember);
        Reservation reservation = reservationRepository.findByIdAndMember(reservationId, member)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회의실 예약 입니다."));

        LocalDate reservationDate = reservation.getReservationDate();
        LocalTime newStartAt = request.newStartAt();
        LocalTime newEndAt = request.newEndAt();
        validateReservationDateTime(reservationDate, newStartAt, newEndAt);

        if (isAlreadyReserved(reservation.getMeetingRoom(), reservationDate, newStartAt, newEndAt)) {
            throw new AlreadyInUseException("해당 시간에 예약이 이미 존재합니다.");
        }
        reservation.changeReservationTime(newStartAt, newEndAt);
        validateTotalReservationTime(reservation);

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void cancel(final Long reservationId, final LoginMember loginMember) {
        Member member = getMember(loginMember);

        if (!reservationRepository.existsByIdAndMember(reservationId, member)) {
            throw new EntityNotFoundException("존재하지 않는 예약 입니다.");
        }

        reservationRepository.deleteByIdAndMember(reservationId, member);
    }

    private MeetingRoom getMeetingRoom(final String meetingRoomName) {
        return meetingRoomRepository.findByRoomName(meetingRoomName)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회의실 입니다."));
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자 입니다."));
    }

    public List<ReservationResponse> getReservationsByRoomAndDate(final Long roomId, final LocalDate reservationDate) {
        return reservationRepository.findByMeetingRoomIdAndReservationDate(roomId, reservationDate)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
