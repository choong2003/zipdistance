package com.sc.zipdistance.controller;

import com.sc.zipdistance.model.dto.BaseResponse;
import com.sc.zipdistance.model.dto.CalculateDistanceDto;
import com.sc.zipdistance.model.dto.CalculateDistanceResponseDto;
import com.sc.zipdistance.model.dto.PostcodeDto;
import com.sc.zipdistance.service.PostcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * created 18/04/2025 - 15:32
 * project zipdistance
 * author sc
 */
@RestController
@RequestMapping("/api/v1/admin/postcode")
public class PostcodeController {

    @Autowired
    private PostcodeService postcodeService;

    @Operation(summary = "Create a new postcode", description = "Requires 'CREATE_POSTAL' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PostCode created successfully"),
            @ApiResponse(responseCode = "400", description = "PostCode already exists or creation failed")
    })
    @PreAuthorize("hasAuthority('CREATE_POSTAL')")
    @PostMapping
    public BaseResponse<PostcodeDto> createPostcode(@RequestBody PostcodeDto createPostcode) {
        // Call the service to create a postcode
        if (createPostcode != null) {
            PostcodeDto createdPostcode = postcodeService.createNewPostcode(createPostcode);
            if (createdPostcode == null) {
                return new BaseResponse<PostcodeDto>(null, "ERROR", "PostCode already exists");
            }
            return new BaseResponse<>(createdPostcode, "SUCCESS", "PostCode created successfully");
        }
        return new BaseResponse<PostcodeDto>(null, "ERROR", "PostCode creation failed");
    }

    @Operation(summary = "Update a postcode", description = "Requires 'UPDATE_POSTAL' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PostCode updated successfully"),
            @ApiResponse(responseCode = "400", description = "PostCode update failed")
    })
    @PreAuthorize("hasAuthority('UPDATE_POSTAL')")
    @PutMapping
    public BaseResponse<PostcodeDto> updatePostcode(@RequestBody PostcodeDto updatePostcode) {
        // Call the service to update a postcode
        if (updatePostcode != null) {
            PostcodeDto updatedPostcode = postcodeService.updatePostcode(updatePostcode);
            return new BaseResponse<>(updatedPostcode, "SUCCESS", "PostCode updated successfully");
        }
        return new BaseResponse<PostcodeDto>(null, "ERROR", "PostCode update failed");
    }

    @Operation(summary = "Get postcode by value", description = "Requires 'VIEW_POSTAL' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PostCode retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "PostCode not found")
    })
    @PreAuthorize("hasAuthority('VIEW_POSTAL')")
    @GetMapping("/{postcode}")
    public BaseResponse<PostcodeDto> getPostcodeById(@PathVariable String postcode) {
        // Call the service to get postcode by postcode
        if (postcode != null) {
            PostcodeDto postcodeDto = postcodeService.findPostcodeByPostcode(postcode);
            if (postcodeDto == null) {
                return new BaseResponse<PostcodeDto>(null, "ERROR", "PostCode not found");
            }
            return new BaseResponse<>(postcodeDto, "SUCCESS", "PostCode retrieved successfully");
        }
        return new BaseResponse<PostcodeDto>(null, "ERROR", "PostCode retrieval failed");
    }

    @Operation(summary = "Calculate distance between two postcodes", description = "Requires 'CALCULATE_DISTANCE' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Distance calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Unable to calculate the distance")
    })
    @PreAuthorize("hasAuthority('CALCULATE_DISTANCE')")
    @PostMapping("/calculate")
    public BaseResponse<CalculateDistanceResponseDto> calculatePostcodeDistance(@RequestBody CalculateDistanceDto calculateDistanceDto) {
        if (calculateDistanceDto != null) {
            CalculateDistanceResponseDto calculateDistanceResponseDto = postcodeService.calculateDistance(calculateDistanceDto.getPostcode1(),
                    calculateDistanceDto.getPostcode2());
            if (calculateDistanceResponseDto == null) {
                return new BaseResponse<CalculateDistanceResponseDto>(null, "ERROR", "Unable to calculate the distance");
            }
            return new BaseResponse<>(calculateDistanceResponseDto, "SUCCESS", "Distance calculated successfully");
        }
        return new BaseResponse<CalculateDistanceResponseDto>(null, "ERROR", "Unable to calculate the distance");
    }

}
