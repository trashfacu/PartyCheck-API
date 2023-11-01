package com.partyCheck.Backend.service;

import com.partyCheck.Backend.entity.Image;
import com.partyCheck.Backend.entity.Venue;
import com.partyCheck.Backend.repository.IImageRepository;
import com.partyCheck.Backend.repository.IS3Service;
import com.partyCheck.Backend.repository.IVenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements IS3Service {

    private final S3Client s3Client;
    private final IImageRepository imageRepository;
    private final IVenueRepository venueRepository;
    @Override
    public String uploadFile(MultipartFile file, Integer venueId) throws IOException {
        try{
            String fileName = file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("party-check-s3-storage")
                    .key(fileName)
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            //Get the url from S3
            String imageUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket("party-check-s3-storage")
                    .key(fileName)
                    .build()).toExternalForm();
            //Save url in db and link it to a venue
            Optional<Venue> optionalVenue = venueRepository.findById(venueId);
            if (optionalVenue.isPresent()) {
                Venue venue = optionalVenue.get();
                Image image = new Image();
                image.setTitle(fileName);
                image.setUrl(imageUrl);
                image.setVenue(venue);
                imageRepository.save(image);
                return "Image uploaded and associated to the Venue correctly";
            }else {
                return "Venue not found with the provided ID";
            }
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public String deleteFile (String fileName)throws IOException{
        if (!doesObjectExist(fileName)){
            return "EL archivo introducido no existe";
        }
        try {
            // Delete url from db
            String imageUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket("party-check-s3-storage")
                    .key(fileName)
                    .build()).toExternalForm();
            Image imageToDelete = imageRepository.findByUrl(imageUrl);
            imageRepository.delete(imageToDelete);
            //Delete object form S3
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket("party-check-s3-storage")
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            return "File deleted";
        }catch (S3Exception e){
            throw new IOException(e.getMessage());
        }
    }
    @Override
    public List<String> listFiles() throws IOException {
        try {
            List<Image> images = imageRepository.findAll();
            List<String> fileUrls = new ArrayList<>();
            for (Image image : images) {
                fileUrls.add(image.getUrl());
            }
            return fileUrls;
        }catch (S3Exception e){
            throw new IOException(e.getMessage());
        }
    }

    private boolean doesObjectExist(String objectKey){
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket("party-check-s3-storage")
                    .key(objectKey)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        }catch (S3Exception e){
            if (e.statusCode() == 404){
                return false;
            }
        }
        return true;
    }

}

