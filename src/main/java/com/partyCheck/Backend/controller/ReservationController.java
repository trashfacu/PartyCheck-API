package com.partyCheck.Backend.controller;

import com.partyCheck.Backend.entity.Reservation;
import com.partyCheck.Backend.entity.ReservationStatus;
import com.partyCheck.Backend.model.ReservationDTO;
import com.partyCheck.Backend.repository.IUserRepository;
import com.partyCheck.Backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create/{venueId}/{userId}")
    public ResponseEntity<String> createReservation (
            @PathVariable Integer venueId,
            @PathVariable Integer userId,
            @RequestBody ReservationDTO reservationDTO
    ){
        try {
            reservationService.createReservation(venueId, userId, reservationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Venue booked correctly.");
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getReason());
            }
            throw e;
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error when booking.");
        }
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByVenue(@PathVariable Integer venueId) throws Exception{
        try {
            List<ReservationDTO> reservations = reservationService.getReservationsByVenue(venueId);
            return ResponseEntity.ok(reservations);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            throw e;
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
