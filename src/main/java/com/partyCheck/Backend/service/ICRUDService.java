package com.partyCheck.Backend.service;

import com.partyCheck.Backend.model.VenueDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICRUDService <DTO, Entity>{
    void create(DTO dto) throws Exception;

    DTO read(Integer id) throws Exception;

    void delete(Integer id) throws Exception;

    void update(DTO dto) throws Exception;

    List<DTO> getAlL();
}
