package com.sc.zipdistance.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * created 18/04/2025 - 14:57
 * project zipdistance
 * author sc
 */
@Data
public class PostcodeDto {

    private String postcode;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
