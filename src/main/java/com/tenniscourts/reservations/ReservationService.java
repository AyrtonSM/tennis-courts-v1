package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestDTO;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleMapper;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

    @Autowired
    private final ReservationRepository reservationRepository;
    @Autowired
    private final GuestRepository guestRepository;
    @Autowired
    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;
    private final ReservationMapper reservationMapper;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Reservation reservation = new Reservation();
        Optional<Guest> guest = this.guestRepository.findById(createReservationRequestDTO.getGuestId());
        if(!guest.isPresent()){
            throw new EntityNotFoundException("Guest not found.");
        }

        Optional<Schedule> scheduleOptional = this.scheduleRepository.findById(createReservationRequestDTO.getScheduleId());
        if(!scheduleOptional.isPresent()){
            throw new EntityNotFoundException("Schedule not found.");
        }
        reservation.setSchedule(scheduleOptional.get());
        reservation.setGuest(guest.get());
        reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);
        reservation.setValue(new BigDecimal(10));
        this.reservationRepository.saveAndFlush(reservation);
        return this.reservationMapper.map(reservation);

    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOptional = this.reservationRepository.findById(reservationId);
        if(!reservationOptional.isPresent()){
            throw new EntityNotFoundException("Reservation not found.");
        }

        Reservation reservation = reservationOptional.get();
        RescheduleReservationDTO rescheduleReservationDTO = new RescheduleReservationDTO();
        rescheduleReservationDTO.setScheduleId(reservation.getSchedule().getId());
        rescheduleReservationDTO.setPreviousReservationId(reservation.getId());
        rescheduleReservationDTO.setStartDateTime(reservation.getSchedule().getStartDateTime());
        rescheduleReservationDTO.setEndDateTime(reservation.getSchedule().getEndDateTime());

        return reservationMapper.map(this.cancel(rescheduleReservationDTO));
    }

    private Reservation cancel(RescheduleReservationDTO rescheduleReservationDTO) {
        return reservationRepository.findById(rescheduleReservationDTO.getPreviousReservationId()).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED, rescheduleReservationDTO);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status,RescheduleReservationDTO rescheduleReservationDTO) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        Schedule schedule = new Schedule();
        schedule.setTennisCourt(reservation.getSchedule().getTennisCourt());
        schedule.setStartDateTime(rescheduleReservationDTO.getStartDateTime());
        schedule.setEndDateTime(rescheduleReservationDTO.getEndDateTime());

        this.scheduleRepository.saveAndFlush(schedule);

        reservation.setSchedule(schedule);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        BigDecimal refundPercentage = null;

        if (hours >= 12){
            refundPercentage = BigDecimal.valueOf(0.25);

            return reservation.getValue().multiply(refundPercentage) ;
        }

        if (hours >= 2){
            refundPercentage = BigDecimal.valueOf(0.5);
            return reservation.getValue().multiply(refundPercentage) ;
        }

        if (hours >= 0.01){
            refundPercentage = BigDecimal.valueOf(0.75);;
            return reservation.getValue().multiply(refundPercentage) ;
        }

        return BigDecimal.ZERO;
    }

    /*TODO: This method actually not fully working, find a way to fix the issue when it's throwing the error:
            "Cannot reschedule to the same slot.*/
    public ReservationDTO rescheduleReservation(RescheduleReservationDTO rescheduleReservationDTO) {
        Reservation previousReservation = cancel(rescheduleReservationDTO);

        if (rescheduleReservationDTO.getScheduleId().equals(previousReservation.getSchedule().getId())) {
                throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(rescheduleReservationDTO.getScheduleId())
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }

    public List<ReservationDTO> findReservations() {
        List<Reservation> reservations = this.reservationRepository.findAll();
        return this.reservationMapper.map(reservations);
    }
}
