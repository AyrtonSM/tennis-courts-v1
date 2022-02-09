package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.List;

@RestController(value = "reservation")
@AllArgsConstructor
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @PostMapping("reservation")
    @ApiOperation("Saves/Books a new reservation into the system")
    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @GetMapping("reservation/{reservationId}")
    @ApiOperation("Finds a previously booked reservation")
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable  Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @GetMapping("reservation")
    @ApiOperation("Finds a previously booked reservation")
    public ResponseEntity<List<ReservationDTO>> findReservation() {
        return ResponseEntity.ok(reservationService.findReservations());
    }

    @DeleteMapping("reservation/{reservationId}")
    @ApiOperation("Cancel an existing reservation")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @PutMapping("reservation")
    @ApiOperation("Re-schedule an existing reservation")
    public ResponseEntity<ReservationDTO> rescheduleReservation(@RequestBody RescheduleReservationDTO rescheduleReservationDTO) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(rescheduleReservationDTO));
    }
}
