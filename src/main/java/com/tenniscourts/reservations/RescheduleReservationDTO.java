package com.tenniscourts.reservations;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class RescheduleReservationDTO {

    @NotNull
    private Long previousReservationId;
    @NotNull
    private Long scheduleId;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm")
    @NotNull
    private LocalDateTime startDateTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm")
    @NotNull
    private LocalDateTime endDateTime;


}
