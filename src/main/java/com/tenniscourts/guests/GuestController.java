package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "guest")
public class GuestController extends BaseRestController {

    @Autowired
    private GuestService guestService;

    @GetMapping("guest")
    @ApiOperation("Returns a complete list of guests")
    public ResponseEntity<List<GuestDTO>> listGuests(){
        List<GuestDTO> guests = this.guestService.listGuests();
        return ResponseEntity.ok(guests);
    }

    @PostMapping("guest")
    @ApiOperation("Adds a new guest into the system")
    public ResponseEntity<Void> addGuest(@RequestBody GuestDTO guestDTO){
        return ResponseEntity.created(locationByEntity(this.guestService.addGuest(guestDTO).getId())).build();

    }

    @PutMapping("guest")
    @ApiOperation("Updates an existing guest in the system")
    public ResponseEntity<Void> updateGuest(@RequestBody GuestDTO guestDTO){
        this.guestService.updateGuest(guestDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping("guest/{id}")
    @ApiOperation("Returns a guest based on a requested id")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long id){
        GuestDTO guest = this.guestService.getGuestById(id);
        return ResponseEntity.ok(guest);
    }

    @DeleteMapping("guest/{id}")
    @ApiOperation("Returns a guest based on a requested name")
    public ResponseEntity<GuestDTO> deleteById(@PathVariable Long id){
        ;
        return ResponseEntity.ok(this.guestService.deleteById(id));
    }


    @GetMapping("guest/name/{name}")
    @ApiOperation("Returns a guest based on a requested name")
    public ResponseEntity<GuestDTO> getGuestByName(@PathVariable String name){
        GuestDTO guest = this.guestService.getGuestByName(name);
        return ResponseEntity.ok(guest);
    }




}
