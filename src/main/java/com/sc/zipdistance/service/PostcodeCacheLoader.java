package com.sc.zipdistance.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * created 18/04/2025 - 15:15
 * project zipdistance
 * author sc
 */
@Component
public class PostcodeCacheLoader {

    @Autowired
    private PostcodeService postcodeService;

    @PostConstruct
    public void preload() {
        postcodeService.retreiveAllPostcode();
    }
}
