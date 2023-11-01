package com.partyCheck.Backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyCheck.Backend.entity.Reservation;
import com.partyCheck.Backend.entity.User;
import com.partyCheck.Backend.entity.Venue;
import com.partyCheck.Backend.model.ReservationDTO;
import com.partyCheck.Backend.repository.IReservationRepository;
import com.partyCheck.Backend.repository.IUserRepository;
import com.partyCheck.Backend.repository.IVenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final IReservationRepository reservationRepository;
    private final IUserRepository userRepository;
    private final ObjectMapper mapper;
    private final IVenueRepository venueRepository;

    public void createReservation(Integer venueId, Integer userId, ReservationDTO reservationDTO) throws Exception {
        try {
            Reservation reservation = mapper.convertValue(reservationDTO, Reservation.class);

            // Asigna las fechas y horas adecuadas
            reservation.setStartDate(reservationDTO.getStartDate());
            reservation.setEndDate(reservationDTO.getEndDate());
            reservation.setStartTime(reservationDTO.getStartTime());
            reservation.setEndTime(reservationDTO.getEndTime());

            //Get the venue
            Venue venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
            //Get the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            //Associate to venue and user
            reservation.setVenue(venue);
            reservation.setUser(user);

            reservationRepository.save(reservation);
        }catch (Exception e){
            throw new Exception("Error when booking" + e.getMessage());
        }
    }

    public List<ReservationDTO> getReservationsByVenue(Integer venueId) throws Exception {
        try {
            // Registro para verificar el valor de venueId
            System.out.println("Venue ID recibido: " + venueId);

            // Realiza una consulta a la base de datos para obtener todas las reservas con el venueId dado.
            List<Reservation> reservations = reservationRepository.findByVenueVenueId(venueId);

            // Verifica si se encontraron reservas.
            if (reservations.isEmpty()) {
                System.out.println("No se encontraron reservas para el venue con ID: " + venueId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reservations found for the venue");
            }

            // Registro para verificar las reservas recuperadas
            System.out.println("Reservas encontradas para el venue con ID " + venueId + ": " + reservations.size());

            // Convierte las reservas en ReservationDTO.

            return reservations.stream()
                    .map(reservation -> mapper.convertValue(reservation, ReservationDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Registro de errores
            System.err.println("Error obteniendo reservas por venue: " + e.getMessage());
            throw new Exception("Error getting reservations by venue: " + e.getMessage());
        }
    }



}