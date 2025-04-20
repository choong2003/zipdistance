package com.sc.zipdistance.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * created 18/04/2025 - 12:55
 * project zipdistance
 * author sc
 */
@Entity
@Getter
@Setter
@Table(name = "postcodelatlng")
public class Postcode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", sequenceName = "postcode_id_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false, length = 8)
    private String postcode;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

}
