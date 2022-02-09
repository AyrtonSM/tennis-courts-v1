package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.BusinessException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.exceptions.ErrorDetails;
import com.tenniscourts.tenniscourts.TennisCourt;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {

        List<ScheduleDTO> schedulesDTO = this.findSchedulesByTennisCourtId(tennisCourtId);
        Iterator<ScheduleDTO> scheduleDTOIterator = schedulesDTO.iterator();

        while(scheduleDTOIterator.hasNext()){
            ScheduleDTO scheduleDTO = scheduleDTOIterator.next();

            LocalDateTime startDateTime = scheduleDTO.getStartDateTime();
            LocalDateTime endDateTime = scheduleDTO.getEndDateTime();

            if(createScheduleRequestDTO.getStartDateTime().isEqual(startDateTime) ||
                    (createScheduleRequestDTO.getStartDateTime().isAfter(startDateTime) &&
                        createScheduleRequestDTO.getStartDateTime().isBefore(endDateTime))) {

                throw new BusinessException("Cannot overwrite an existing schedule with the passed date and times");
            }
        }

        Schedule schedule = new Schedule();
        TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setId(createScheduleRequestDTO.getTennisCourtId());
        long time = ChronoUnit.HOURS.between(createScheduleRequestDTO.getStartDateTime(), createScheduleRequestDTO.getEndDateTime());

        if (time != 1){
            throw new BusinessException("Guests need to always play for 1 hour. Date and time mentioned are not respecting this rule");
        }

        schedule.setStartDateTime(createScheduleRequestDTO.getStartDateTime());
        schedule.setEndDateTime(createScheduleRequestDTO.getEndDateTime());
        schedule.setTennisCourt(tennisCourt);

        this.scheduleRepository.save(schedule);

        return this.scheduleMapper.map(schedule);
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        //TODO: implement
        return null;
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        Optional<Schedule> scheduleOptional = this.scheduleRepository.findById(scheduleId);
        if (!scheduleOptional.isPresent()){
            throw new EntityNotFoundException("Entity wasn't found");
        }

        return this.scheduleMapper.map(scheduleOptional.get());
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
