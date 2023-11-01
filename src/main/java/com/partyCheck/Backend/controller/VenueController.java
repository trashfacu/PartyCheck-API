package com.partyCheck.Backend.controller;

import com.partyCheck.Backend.entity.Category;
import com.partyCheck.Backend.entity.Venue;
import com.partyCheck.Backend.model.VenueDTO;
import com.partyCheck.Backend.repository.ICategoryRepository;
import com.partyCheck.Backend.repository.IVenueRepository;
import com.partyCheck.Backend.service.S3ServiceImp;
import com.partyCheck.Backend.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueController {


    private final VenueService venueService;
    private final IVenueRepository venueRepository;
    private final ICategoryRepository categoryRepository;
    private final S3ServiceImp s3Service;

    //POST

    @PostMapping
    public ResponseEntity<String> addVenue(@RequestBody VenueDTO venueDTO) throws Exception {
        try {
            venueService.create(venueDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Venue created");
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getReason());
            }
            throw e;
        }
    }

    @PostMapping("/add-list")
    public ResponseEntity<String> addVenues(@RequestBody List<VenueDTO> venueDTOList) throws Exception {
        try {
            for (VenueDTO venueDTO: venueDTOList) {
                venueService.create(venueDTO);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Venues created");
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getReason());
            }
            throw e;
        }
    }
        // GET

    @GetMapping("/{id}")
    public ResponseEntity<VenueDTO> getVenue(@PathVariable Integer id) throws Exception {
        VenueDTO venueDTO = venueService.read(id);
        return ResponseEntity.ok(venueDTO);
    }

    @GetMapping("/list")
    public List<VenueDTO> listVenues() {
        return venueService.getAlL();
    }


    @GetMapping("/")
    public Page<VenueDTO> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return venueService.getPage(pageRequest);
    }

    @GetMapping("/{venueId}/images")
    public ResponseEntity<List<String>> getVenueImages (@PathVariable Integer venueId){
        try {
            List<String> imageUrls = venueService.getVenueImagesUrls(venueId);
            return ResponseEntity.ok(imageUrls);
        }catch (ResponseStatusException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<VenueDTO>> recommendVenues() {
        try {
            List<VenueDTO> recommendedVenues = venueService.getRecommendedVenues(4);
            return new ResponseEntity<>(recommendedVenues, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/{startDate}/{endDate}")
    public ResponseEntity<List<Integer>> getAvailableVenuesByDateRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Integer> availableVenues = venueService.getAvailableVenuesByDateRange(startDate, endDate);
            return new ResponseEntity<>(availableVenues, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // PUT

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVenue(@PathVariable Integer id, @RequestBody VenueDTO venueDTO) throws Exception {
        venueDTO.setVenueId(id);
        venueService.update(venueDTO);
        return ResponseEntity.ok("Venue updated");
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<String> deleteVenue(@PathVariable Integer venueId) throws Exception{
        try {
            venueService.delete(venueId);
            return ResponseEntity.ok("Venue deleted");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{venueName}/update-category")
    public ResponseEntity<String> updateVenueCategory(@PathVariable String venueName, @RequestParam String newCategoryName) {
        Optional<Venue> optionalVenue = venueRepository.findByVenueName(venueName);
        Optional<Category> optionalNewCategory = categoryRepository.findByCategoryName(newCategoryName);

        if (optionalVenue.isPresent() && optionalNewCategory.isPresent()){
            Venue venue = optionalVenue.get();
            Category newCategory = optionalNewCategory.get();

            venue.setCategory(newCategory);
            venueRepository.save(venue);

            return ResponseEntity.ok("Category updated");
        }else{
            return ResponseEntity.badRequest().body("Venue or Category not found");
        }

    }
}

