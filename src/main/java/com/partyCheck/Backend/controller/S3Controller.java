package com.partyCheck.Backend.controller;

import com.partyCheck.Backend.entity.Image;
import com.partyCheck.Backend.repository.IImageRepository;
import com.partyCheck.Backend.repository.IS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

    private final IS3Service s3Service;
    private final IImageRepository imageRepository;

    /*@PostMapping("/upload/{venueId}")
    public String uploadFile(@PathVariable Integer venueId, @RequestParam("file")MultipartFile file) throws IOException {
        return s3Service.uploadFile(file, venueId);
    }
    */

    @PostMapping("upload/{venueId}")
    public String uploadFile(@PathVariable Integer venueId, @RequestParam("file")MultipartFile[] files) throws IOException{
        for (MultipartFile file : files){
            s3Service.uploadFile(file,venueId);
        }
        return "Images uploaded correctly";
    }

    @GetMapping("/list")
    public List<String> getAllObjects() throws IOException {
        try {
            List<Image> images = imageRepository.findAll();
            List<String> fileUrls  = new ArrayList<>();
            for (Image image : images){
                fileUrls.add(image.getUrl());
            }
            return fileUrls;
        }catch (Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public String deleteFileName(@PathVariable("fileName") String fileName) throws IOException {
        return s3Service.deleteFile(fileName);
    }

}
