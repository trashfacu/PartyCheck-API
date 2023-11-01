package com.partyCheck.Backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicesDTO {

    private Integer servicesId;
    private String name;
    private String description;
    private Double price;
}
