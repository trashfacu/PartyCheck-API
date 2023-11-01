package com.partyCheck.Backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "venue")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Integer venueId;

    @Column (name = "name")
    private String venueName;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "capacity")
    private Integer venueCapacity;

    @Column(name = "price")
    private BigDecimal price;

    @OneToMany(mappedBy = "venue")
    @JsonIgnore
    private List<Image> venueImages;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "venue")
    @JsonIgnore
    private List<Reservation> reservations;
/*
    @OneToMany(mappedBy = "venue")
    private List<Service> services;
 */
}

