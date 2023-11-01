package com.partyCheck.Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "image")
@RequiredArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    /*
    @ManyToOne
    @JoinColumn(name = "services_id")
    private Service service; */

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

}