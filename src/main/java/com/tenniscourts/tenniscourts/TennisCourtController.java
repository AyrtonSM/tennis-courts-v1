package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController(value="tennis-court")
@AllArgsConstructor
public class TennisCourtController extends BaseRestController {


    private final TennisCourtService tennisCourtService;

    @PostMapping("tennis-court")
    @ApiOperation("Creates a new Tennis Court")
    public ResponseEntity<Void> addTennisCourt(@RequestBody TennisCourtDTO tennisCourtDTO) {
        return ResponseEntity.created(locationByEntity(tennisCourtService.addTennisCourt(tennisCourtDTO).getId())).build();
    }

    @GetMapping("tennis-court/{tennisCourtId}")
    @ApiOperation("Retrieves an existing Tennis Court based on its id")
    public ResponseEntity<TennisCourtDTO> findTennisCourtById(@PathVariable Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtById(tennisCourtId));
    }

    @GetMapping("tennis-court/{tennisCourtId}/schedules")
    @ApiOperation("Find a tennis court with schedules based on the tennis court's id")
    public ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(@PathVariable Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId));
    }
}
