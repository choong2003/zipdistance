package com.sc.zipdistance.service;

import com.sc.zipdistance.model.dto.CalculateDistanceResponseDto;
import com.sc.zipdistance.model.dto.PostcodeDto;
import com.sc.zipdistance.model.entity.Postcode;
import com.sc.zipdistance.repository.PostcodeRepository;
import com.sc.zipdistance.util.DistanceCalculatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostcodeService {

    public final String POSTCODE_DISTANCE_UNIT = "km";

    @Autowired
    private PostcodeRepository postcodeRepository;
    @Autowired
    private DistanceCalculatorUtil distanceCalculatorUtil;

    @Cacheable("postalCodes")
    public List<PostcodeDto> retreiveAllPostcode() {

        List<Postcode> allPostcodes = postcodeRepository.findAll();
        return allPostcodes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public CalculateDistanceResponseDto calculateDistance(String postcode1, String postcode2) {
        PostcodeDto firstPostcode = findPostcodeByPostcode(postcode1);
        PostcodeDto secondPostcode = findPostcodeByPostcode(postcode2);

        if (firstPostcode != null && secondPostcode != null) {
            double latitude1 = firstPostcode.getLatitude().doubleValue();
            double longitude1 = firstPostcode.getLongitude().doubleValue();

            double latitude2 = secondPostcode.getLatitude().doubleValue();
            double longitude2 = secondPostcode.getLongitude().doubleValue();

            double result = distanceCalculatorUtil.calculateDistance(latitude1, longitude1, latitude2, longitude2);

            CalculateDistanceResponseDto response = new CalculateDistanceResponseDto();
            response.setPostcodeDto1(firstPostcode);
            response.setPostcodeDto2(secondPostcode);
            response.setDistance(result);
            response.setUnit(POSTCODE_DISTANCE_UNIT);
            return response;

        }
        return null;
    }

    public PostcodeDto findPostcodeByPostcode(String postcode) {
        Postcode foundPostcode = postcodeRepository.findByPostcode(postcode);
        if (foundPostcode != null) {
            return convertToDto(foundPostcode);
        }
        return null;
    }

    @CacheEvict
    public PostcodeDto createNewPostcode(PostcodeDto postcodeDto) {

        if (postcodeDto != null && StringUtils.hasLength(postcodeDto.getPostcode())) {
            Postcode existingPostcode = postcodeRepository.findByPostcode(postcodeDto.getPostcode());
            if (existingPostcode == null) {
                Postcode newPostcode = new Postcode();
                newPostcode.setPostcode(postcodeDto.getPostcode());
                newPostcode.setLongitude(postcodeDto.getLongitude());
                newPostcode.setLatitude(postcodeDto.getLatitude());
                Postcode savedPostcode = postcodeRepository.save(newPostcode);

                return convertToDto(savedPostcode);
            }
        }
        return null;
    }

    @CacheEvict
    public PostcodeDto updatePostcode(PostcodeDto postcodeDto) {

        if (postcodeDto != null && StringUtils.hasLength(postcodeDto.getPostcode())) {
            Postcode existingPostcode = postcodeRepository.findByPostcode(postcodeDto.getPostcode());
            if (existingPostcode != null) {
                existingPostcode.setLongitude(postcodeDto.getLongitude());
                existingPostcode.setLatitude(postcodeDto.getLatitude());
                Postcode updatedPostcode = postcodeRepository.save(existingPostcode);

                return convertToDto(updatedPostcode);
            }
        }
        return null;
    }


    private PostcodeDto convertToDto(Postcode postcode) {
        PostcodeDto postcodeDto = new PostcodeDto();
        postcodeDto.setPostcode(postcode.getPostcode());
        postcodeDto.setLongitude(postcode.getLongitude());
        postcodeDto.setLatitude(postcode.getLatitude());

        return postcodeDto;
    }
}
