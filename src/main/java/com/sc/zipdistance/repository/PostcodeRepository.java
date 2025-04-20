package com.sc.zipdistance.repository;

import com.sc.zipdistance.model.entity.Postcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * created 18/04/2025 - 14:52
 * project zipdistance
 * author sc
 */
@Repository
public interface PostcodeRepository extends JpaRepository<Postcode, Long> {

    Postcode findByPostcode(String postcode);

    Postcode findByLatitudeAndLongitude(Double latitude, Double longitude);

}
