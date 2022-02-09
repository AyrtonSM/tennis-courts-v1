package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController("schedule")
@AllArgsConstructor
public class ScheduleController extends BaseRestController {

    @Autowired
    private final ScheduleService scheduleService;

    @PostMapping("schedule")
    @ApiOperation("Schedules a Tennis Court")
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO){
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO).getId())).build();
    }

    @GetMapping("schedule/dates/{startDate}/{endDate}")
    @ApiOperation("Find all schedules based on a starting Date and an ending Date")
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate startDate,
                                                                  @PathVariable @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59))));
    }

    @GetMapping("schedule/{scheduleId}")
    @ApiOperation("Schedules a Tennis Court")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }
}
