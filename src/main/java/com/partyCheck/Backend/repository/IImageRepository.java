package com.partyCheck.Backend.repository;

import com.partyCheck.Backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IImageRepository extends JpaRepository<Image, Integer> {
    Image findByUrl(String url);
}
