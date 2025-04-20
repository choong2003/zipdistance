package com.sc.zipdistance.model.dto;

import lombok.Data;

/**
 * created 18/04/2025 - 15:40
 * project zipdistance
 * author sc
 */
@Data
public class CalculateDistanceResponseDto {

    public PostcodeDto postcodeDto1;
    public PostcodeDto postcodeDto2;

    public double distance;
    public String unit;
}
