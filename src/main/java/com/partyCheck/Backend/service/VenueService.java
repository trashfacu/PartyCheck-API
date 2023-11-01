package com.partyCheck.Backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyCheck.Backend.entity.Category;
import com.partyCheck.Backend.entity.Image;
import com.partyCheck.Backend.entity.Reservation;
import com.partyCheck.Backend.entity.Venue;
import com.partyCheck.Backend.model.VenueDTO;
import com.partyCheck.Backend.repository.ICategoryRepository;
import com.partyCheck.Backend.repository.IImageRepository;
import com.partyCheck.Backend.repository.IReservationRepository;
import com.partyCheck.Backend.repository.IVenueRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueService implements ICRUDService<VenueDTO, Venue> {

    private static final Logger logger = Logger.getLogger(VenueService.class);
    @Lazy
    private final IVenueRepository venueRepository;
    private final ICategoryRepository categoryRepository;
    private final IImageRepository imageRepository;
    private final IReservationRepository reservationRepository;
    private final S3ServiceImp s3Service;
    private final ObjectMapper mapper;

    @Override
    public void create(VenueDTO venueDTO) throws Exception {
        //Validate that venue cannot be null
        if (venueDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue can't be null.");
        }
        if (venueRepository.existsByVenueName(venueDTO.getVenueName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Venue name already in database!");
        }
        try {
            //Convert DTO to Class
            Venue venue = mapper.convertValue(venueDTO, Venue.class);

            Optional<Category> optionalCategory = categoryRepository.findByCategoryName(venueDTO.getCategoryName());
            if (optionalCategory.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            venue.setCategory(optionalCategory.get());

            venueRepository.save(venue);
        } catch (Exception exception) {
            //Logging the error for troubleshooting
            logger.error("An error occurred while saving the venue");
            throw exception;
        }
    }

    @Override
    public VenueDTO read(Integer id) throws Exception {
        //validate that the id is not null or less than zero
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid venue ID");
        }
        //check if the venue already exist in the database
        Optional<Venue> venue = venueRepository.findById(id);
        if (venue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        }
        return convertToDTO(venue.get());
    }

    @Override
    public void delete(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid venue ID");
        }
        //check if the venue exist
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        //Delete image
        if (venue.getVenueImages() != null && !venue.getVenueImages().isEmpty()) {
            for (Image image : venue.getVenueImages()) {
                s3Service.deleteFile(image.getTitle());
                imageRepository.delete(image);
            }
        }
        venueRepository.deleteById(id);
    }

    @Override
    public void update(VenueDTO venueDTO) throws Exception {
        // Validate that the DTO is not null
        if (venueDTO == null) {
            throw new IllegalArgumentException("VenueDTO cannot be null");
        }
        // Check if the venue id is valid
        Integer venueId = venueDTO.getVenueId();
        if (venueId == null || venueId <= 0) {
            throw new IllegalArgumentException("Invalid venue ID");
        }
        // Check if the venue exists in the database
        if (!venueRepository.existsById(venueId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        }
        // Convert VenueDTO to Venue entity and set the Venue ID explicitly
        Venue venue = mapper.convertValue(venueDTO, Venue.class);
        venue.setVenueId(venueId);
        venueRepository.save(venue);
    }

    //GetALL And GetByID sin permisos
    @Override
    public List<VenueDTO> getAlL() {
        List<Venue> venueList = venueRepository.findAll();
        System.out.println(venueList.get(1));
        List<VenueDTO> venueDTOList = new ArrayList<>();
        for (Venue venue : venueList) {
            venueDTOList.add(convertToDTO(venue));
        }
        return venueDTOList;
    }

    public Page<VenueDTO> getPage(PageRequest pageRequest) {
        Page<Venue> venuePage = venueRepository.findAll(pageRequest);
        return venuePage.map(this::convertToDTO);
    }



    private VenueDTO convertToDTO(Venue venue) {

        VenueDTO venueDTO = new VenueDTO();

        venueDTO.setVenueId(venue.getVenueId());
        venueDTO.setVenueName(venue.getVenueName());
        venueDTO.setDescription(venue.getDescription());
        venueDTO.setAddress(venue.getAddress());
        venueDTO.setVenueCapacity(venue.getVenueCapacity());
        venueDTO.setVenueImages(venue.getVenueImages());
        venueDTO.setCategoryName(venue.getCategory().getCategoryName());
        venueDTO.setCategoryId(venue.getCategory().getCategoryId());
        venueDTO.setPrice(venue.getPrice());
        return venueDTO;

    }

    public List<String> getVenueImagesUrls(Integer venueId) {
        Optional<Venue> optionalVenue = venueRepository.findById(venueId);
        if (optionalVenue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        }
        Venue venue = optionalVenue.get();
        List<String> imageUrls = new ArrayList<>();
        for (Image image : venue.getVenueImages()) {
            imageUrls.add(image.getUrl());
        }
        return imageUrls;

    }

    public List<VenueDTO> getRecommendedVenues(int venuesCount) {
        List<VenueDTO> allVenues = getAlL();
        List<VenueDTO> recommendedVenues = new ArrayList<>();
        Random random = new Random();

        int totalVenues = allVenues.size();
        if (totalVenues <= venuesCount) {
            return allVenues;
        }
        while (recommendedVenues.size() < venuesCount) {
            int randomIndex = random.nextInt(totalVenues);
            VenueDTO randomVenue = allVenues.get(randomIndex);
            if (!recommendedVenues.contains(randomVenue)) {
                recommendedVenues.add(randomVenue);
            }
        }
        return recommendedVenues;
    }

    public List<Integer> getAvailableVenuesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Integer> venueIdsWithReservationsInDateRange = reservationRepository.findVenueIdsByReservationsDateRange(startDate, endDate);

        List<Integer> allVenueIds = venueRepository.findAllVenueIds();

        //Filtering
        allVenueIds.remove(venueIdsWithReservationsInDateRange);

        List<Integer> limitedResult = allVenueIds.stream().limit(4).toList();

        return limitedResult;
    }

}
