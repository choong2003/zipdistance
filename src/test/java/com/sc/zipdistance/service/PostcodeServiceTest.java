package com.sc.zipdistance.service;

import com.sc.zipdistance.model.dto.CalculateDistanceResponseDto;
import com.sc.zipdistance.model.dto.PostcodeDto;
import com.sc.zipdistance.model.entity.Postcode;
import com.sc.zipdistance.repository.PostcodeRepository;
import com.sc.zipdistance.security.SecurityConfig;
import com.sc.zipdistance.util.DistanceCalculatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * created 20/04/2025 - 11:49
 * project zipdistance
 * author sc
 */
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class PostcodeServiceTest {

    @Mock
    private PostcodeRepository postcodeRepository;

    @Mock
    private DistanceCalculatorUtil distanceCalculatorUtil;

    @InjectMocks
    private PostcodeService postcodeService;

    private Postcode postcode;
    private PostcodeDto postcodeDto;

    @BeforeEach
    void setUp() {
        postcode = new Postcode();
        postcode.setPostcode("12345");
        postcode.setLatitude(new BigDecimal("51.5074"));
        postcode.setLongitude(new BigDecimal("-0.1278"));

        postcodeDto = new PostcodeDto();
        postcodeDto.setPostcode("12345");
        postcodeDto.setLatitude(new BigDecimal("51.5074"));
        postcodeDto.setLongitude(new BigDecimal("-0.1278"));
    }

    // Tests for retreiveAllPostcode
    @Test
    void testRetrieveAllPostcode_Success() {
        List<Postcode> postcodes = Arrays.asList(postcode);
        when(postcodeRepository.findAll()).thenReturn(postcodes);

        List<PostcodeDto> result = postcodeService.retreiveAllPostcode();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).getPostcode());
        assertEquals(new BigDecimal("51.5074"), result.get(0).getLatitude());
        assertEquals(new BigDecimal("-0.1278"), result.get(0).getLongitude());
        verify(postcodeRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllPostcode_EmptyList() {
        when(postcodeRepository.findAll()).thenReturn(Collections.emptyList());

        List<PostcodeDto> result = postcodeService.retreiveAllPostcode();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postcodeRepository, times(1)).findAll();
    }

    // Tests for calculateDistance
    @Test
    void testCalculateDistance_Success() {
        Postcode postcode2 = new Postcode();
        postcode2.setPostcode("67890");
        postcode2.setLatitude(new BigDecimal("48.8566"));
        postcode2.setLongitude(new BigDecimal("2.3522"));

        when(postcodeRepository.findByPostcode("12345")).thenReturn(postcode);
        when(postcodeRepository.findByPostcode("67890")).thenReturn(postcode2);
        when(distanceCalculatorUtil.calculateDistance(
                eq(51.5074), eq(-0.1278), eq(48.8566), eq(2.3522)))
                .thenReturn(343.0);

        CalculateDistanceResponseDto result = postcodeService.calculateDistance("12345", "67890");

        assertNotNull(result);
        assertEquals("12345", result.getPostcodeDto1().getPostcode());
        assertEquals("67890", result.getPostcodeDto2().getPostcode());
        assertEquals(343.0, result.getDistance());
        assertEquals("km", result.getUnit());
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, times(1)).findByPostcode("67890");
        verify(distanceCalculatorUtil, times(1)).calculateDistance(
                eq(51.5074), eq(-0.1278), eq(48.8566), eq(2.3522));
    }

    @Test
    void testCalculateDistance_FirstPostcodeNotFound() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(null);
        when(postcodeRepository.findByPostcode("67890")).thenReturn(new Postcode());

        CalculateDistanceResponseDto result = postcodeService.calculateDistance("12345", "67890");

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, times(1)).findByPostcode("67890");
        verify(distanceCalculatorUtil, never()).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testCalculateDistance_SecondPostcodeNotFound() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(postcode);
        when(postcodeRepository.findByPostcode("67890")).thenReturn(null);

        CalculateDistanceResponseDto result = postcodeService.calculateDistance("12345", "67890");

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, times(1)).findByPostcode("67890");
        verify(distanceCalculatorUtil, never()).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testCalculateDistance_NullInput() {
        CalculateDistanceResponseDto result = postcodeService.calculateDistance(null, "67890");

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode(null);
        verify(distanceCalculatorUtil, never()).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    // Tests for findPostcodeByPostcode
    @Test
    void testFindPostcodeByPostcode_Success() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(postcode);

        PostcodeDto result = postcodeService.findPostcodeByPostcode("12345");

        assertNotNull(result);
        assertEquals("12345", result.getPostcode());
        assertEquals(new BigDecimal("51.5074"), result.getLatitude());
        assertEquals(new BigDecimal("-0.1278"), result.getLongitude());
        verify(postcodeRepository, times(1)).findByPostcode("12345");
    }

    @Test
    void testFindPostcodeByPostcode_NotFound() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(null);

        PostcodeDto result = postcodeService.findPostcodeByPostcode("12345");

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode("12345");
    }

    @Test
    void testFindPostcodeByPostcode_NullInput() {
        PostcodeDto result = postcodeService.findPostcodeByPostcode(null);

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode(null);
    }

    // Tests for createNewPostcode
    @Test
    void testCreateNewPostcode_Success() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(null);
        when(postcodeRepository.save(any(Postcode.class))).thenReturn(postcode);

        PostcodeDto result = postcodeService.createNewPostcode(postcodeDto);

        assertNotNull(result);
        assertEquals("12345", result.getPostcode());
        assertEquals(new BigDecimal("51.5074"), result.getLatitude());
        assertEquals(new BigDecimal("-0.1278"), result.getLongitude());
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, times(1)).save(any(Postcode.class));
    }

    @Test
    void testCreateNewPostcode_PostcodeExists() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(postcode);

        PostcodeDto result = postcodeService.createNewPostcode(postcodeDto);

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    @Test
    void testCreateNewPostcode_NullPostcode() {
        PostcodeDto invalidDto = new PostcodeDto();
        invalidDto.setPostcode(null);

        PostcodeDto result = postcodeService.createNewPostcode(invalidDto);

        assertNull(result);
        verify(postcodeRepository, never()).findByPostcode(anyString());
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    @Test
    void testCreateNewPostcode_EmptyPostcode() {
        PostcodeDto invalidDto = new PostcodeDto();
        invalidDto.setPostcode("");

        PostcodeDto result = postcodeService.createNewPostcode(invalidDto);

        assertNull(result);
        verify(postcodeRepository, never()).findByPostcode(anyString());
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    @Test
    void testCreateNewPostcode_NullInput() {
        PostcodeDto result = postcodeService.createNewPostcode(null);

        assertNull(result);
        verify(postcodeRepository, never()).findByPostcode(anyString());
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    // Tests for updatePostcode
    @Test
    void testUpdatePostcode_Success() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(postcode);
        when(postcodeRepository.save(any(Postcode.class))).thenReturn(postcode);

        PostcodeDto result = postcodeService.updatePostcode(postcodeDto);

        assertNotNull(result);
        assertEquals("12345", result.getPostcode());
        assertEquals(new BigDecimal("51.5074"), result.getLatitude());
        assertEquals(new BigDecimal("-0.1278"), result.getLongitude());
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, times(1)).save(any(Postcode.class));
    }

    @Test
    void testUpdatePostcode_PostcodeNotFound() {
        when(postcodeRepository.findByPostcode("12345")).thenReturn(null);

        PostcodeDto result = postcodeService.updatePostcode(postcodeDto);

        assertNull(result);
        verify(postcodeRepository, times(1)).findByPostcode("12345");
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    @Test
    void testUpdatePostcode_NullPostcode() {
        PostcodeDto invalidDto = new PostcodeDto();
        invalidDto.setPostcode(null);

        PostcodeDto result = postcodeService.updatePostcode(invalidDto);

        assertNull(result);
        verify(postcodeRepository, never()).findByPostcode(anyString());
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    @Test
    void testUpdatePostcode_EmptyPostcode() {
        PostcodeDto invalidDto = new PostcodeDto();
        invalidDto.setPostcode("");

        PostcodeDto result = postcodeService.updatePostcode(invalidDto);

        assertNull(result);
        verify(postcodeRepository, never()).findByPostcode(anyString());
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

    @Test
    void testUpdatePostcode_NullInput() {
        PostcodeDto result = postcodeService.updatePostcode(null);

        assertNull(result);
        verify(postcodeRepository, never()).findByPostcode(anyString());
        verify(postcodeRepository, never()).save(any(Postcode.class));
    }

}