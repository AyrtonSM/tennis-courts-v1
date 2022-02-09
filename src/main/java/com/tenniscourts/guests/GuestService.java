package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private GuestMapper guestMapper;

    public List<GuestDTO> listGuests(){
        List<Guest> guests = this.guestRepository.findAll();
        return guestMapper.map(guests);
    }

    public GuestDTO getGuestById(Long id){
        Optional<Guest> guestOptional = this.guestRepository.findById(id);
        if(!guestOptional.isPresent()){
            throw new EntityNotFoundException("[ERROR] No guest entity was found with the specified id");
        }

        return guestMapper.map(guestOptional.get());
    }
    public GuestDTO getGuestByName(String name){
        Guest guest = this.guestRepository.findGuestByName(name);
        if(guest == null){
            throw new EntityNotFoundException("[ERROR] No guest entity was found with the specified name");
        }
        return guestMapper.map(guest);

    }

    public GuestDTO deleteById(Long id) {
        Optional<Guest> guestOptional = this.guestRepository.findById(id);
        if(!guestOptional.isPresent()){
            throw new EntityNotFoundException("[ERROR] Impossible to complete deletion. No guest entity was found with the specified id");
        }
        this.guestRepository.deleteById(id);
        this.guestRepository.flush();
        return this.guestMapper.map(guestOptional.get());
    }

    public GuestDTO addGuest(GuestDTO guestDTO) {
        Guest guest = this.guestMapper.map(guestDTO);
        this.guestRepository.saveAndFlush(guest);
        return this.guestMapper.map(guest);
    }

    public void updateGuest(GuestDTO guestDTO) {
        Optional<Guest> guestOptional = this.guestRepository.findById(guestDTO.getId());
        if (!guestOptional.isPresent()){
            throw new EntityNotFoundException("[ERROR] Impossible to complete update action. No guest entity was found with the specified id");
        }

        Guest guest = this.guestMapper.map(guestDTO);
        this.guestRepository.saveAndFlush(guest);
    }
}
