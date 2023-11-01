package com.partyCheck.Backend.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter
public class ReservationDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String phone;
    private String city;
    private String country;
    private String postalCode;
}

