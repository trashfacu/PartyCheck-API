package com.partyCheck.Backend.repository;

import com.partyCheck.Backend.entity.Reservation;
import com.partyCheck.Backend.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByVenueVenueId(Integer venueId);

    @Query("SELECT DISTINCT v.venueId FROM Venue v JOIN v.reservations r WHERE (r.startDate BETWEEN :startDate AND :endDate) OR (r.endDate BETWEEN :startDate AND :endDate)")
    List<Integer> findVenueIdsByReservationsDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
