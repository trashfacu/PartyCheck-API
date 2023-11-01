package com.partyCheck.Backend.repository;

import com.partyCheck.Backend.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IVenueRepository extends JpaRepository<Venue, Integer> {
    boolean existsByVenueName(String venueName);

    Optional<Venue> findByVenueName(String venueName);

    @Query("SELECT v.venueId FROM Venue v")
    List<Integer> findAllVenueIds();

}
