package com.partyCheck.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.partyCheck.Backend.entity.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter @Getter
@NoArgsConstructor
public class VenueDTO {

    private Integer venueId;
    private String venueName;
    private String description;
    private String address;
    private Integer venueCapacity;
    private BigDecimal price;
    @JsonIgnore // -> Esto es para no tener un bucle infinito de asignaciones y evitar un StackOverFlow error
    private List<Image> venueImages;
    private String categoryName;
    private Integer categoryId;
}