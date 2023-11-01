package com.partyCheck.Backend.model;

import com.partyCheck.Backend.entity.Venue;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ImageDTO {

    private Integer imageId;
    private String url;
    private String title;
    private Integer venueId;
}
