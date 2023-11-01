package com.partyCheck.Backend.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface IS3Service {
    String uploadFile(MultipartFile file, Integer venueId) throws IOException;
    List<String> listFiles() throws IOException;
    String deleteFile (String fileName)throws IOException;
}
