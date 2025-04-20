package com.sc.zipdistance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc.zipdistance.model.dto.CalculateDistanceDto;
import com.sc.zipdistance.model.dto.CalculateDistanceResponseDto;
import com.sc.zipdistance.model.dto.PostcodeDto;
import com.sc.zipdistance.security.SecurityConfig;
import com.sc.zipdistance.service.PostcodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * created 20/04/2025 - 11:35
 * project zipdistance
 * author sc
 */
@WebMvcTest(PostcodeController.class)
@Import(SecurityConfig.class)
public class PostcodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostcodeService postcodeService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostcodeDto postcodeDto;
    private CalculateDistanceDto calculateDistanceDto;
    private CalculateDistanceResponseDto calculateDistanceResponseDto;

    @BeforeEach
    void setUp() {
        postcodeDto = new PostcodeDto();
        postcodeDto.setPostcode("12345");
        // Add other fields as needed

        calculateDistanceDto = new CalculateDistanceDto();
        calculateDistanceDto.setPostcode1("12345");
        calculateDistanceDto.setPostcode2("67890");

        calculateDistanceResponseDto = new CalculateDistanceResponseDto();
        calculateDistanceResponseDto.setDistance(100.0); // Example field
    }

    // Tests for createPostcode
    @Test
    @WithMockUser(authorities = "CREATE_POSTAL")
    void testCreatePostcode_Success() throws Exception {
        when(postcodeService.createNewPostcode(any(PostcodeDto.class))).thenReturn(postcodeDto);

        mockMvc.perform(post("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postcodeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("PostCode created successfully"))
                .andExpect(jsonPath("$.result.postcode").value("12345"));

        verify(postcodeService, times(1)).createNewPostcode(any(PostcodeDto.class));
    }

    @Test
    @WithMockUser(authorities = "CREATE_POSTAL")
    void testCreatePostcode_Failure_NullInput() throws Exception {
        mockMvc.perform(post("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(postcodeService, never()).createNewPostcode(any());
    }

    @Test
    @WithMockUser(authorities = "CREATE_POSTAL")
    void testCreatePostcode_Failure_PostcodeExists() throws Exception {
        when(postcodeService.createNewPostcode(any(PostcodeDto.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postcodeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("PostCode already exists"))
                .andExpect(jsonPath("$.result").isEmpty());

        verify(postcodeService, times(1)).createNewPostcode(any(PostcodeDto.class));
    }

    @Test
    @WithMockUser(authorities = "VIEW_POSTAL")
    void testCreatePostcode_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postcodeDto)))
                .andExpect(status().isForbidden());

        verify(postcodeService, never()).createNewPostcode(any());
    }

    // Tests for updatePostcode
    @Test
    @WithMockUser(authorities = "UPDATE_POSTAL")
    void testUpdatePostcode_Success() throws Exception {
        when(postcodeService.updatePostcode(any(PostcodeDto.class))).thenReturn(postcodeDto);

        mockMvc.perform(put("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postcodeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("PostCode updated successfully"))
                .andExpect(jsonPath("$.result.postcode").value("12345"));

        verify(postcodeService, times(1)).updatePostcode(any(PostcodeDto.class));
    }

    @Test
    @WithMockUser(authorities = "UPDATE_POSTAL")
    void testUpdatePostcode_Failure_NullInput() throws Exception {
        mockMvc.perform(put("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(postcodeService, never()).updatePostcode(any());
    }

    @Test
    @WithMockUser(authorities = "VIEW_POSTAL")
    void testUpdatePostcode_AccessDenied() throws Exception {
        mockMvc.perform(put("/api/v1/admin/postcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postcodeDto)))
                .andExpect(status().isForbidden());

        verify(postcodeService, never()).updatePostcode(any());
    }

    // Tests for getPostcodeById
    @Test
    @WithMockUser(authorities = "VIEW_POSTAL")
    void testGetPostcodeById_Success() throws Exception {
        when(postcodeService.findPostcodeByPostcode(eq("12345"))).thenReturn(postcodeDto);

        mockMvc.perform(get("/api/v1/admin/postcode/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("PostCode retrieved successfully"))
                .andExpect(jsonPath("$.result.postcode").value("12345"));

        verify(postcodeService, times(1)).findPostcodeByPostcode(eq("12345"));
    }

    @Test
    @WithMockUser(authorities = "VIEW_POSTAL")
    void testGetPostcodeById_Failure_NotFound() throws Exception {
        when(postcodeService.findPostcodeByPostcode(eq("12345"))).thenReturn(null);

        mockMvc.perform(get("/api/v1/admin/postcode/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("PostCode not found"))
                .andExpect(jsonPath("$.result").isEmpty());

        verify(postcodeService, times(1)).findPostcodeByPostcode(eq("12345"));
    }

    @Test
    @WithMockUser(authorities = "CREATE_POSTAL")
    void testGetPostcodeById_AccessDenied() throws Exception {
        mockMvc.perform(get("/api/v1/admin/postcode/12345"))
                .andExpect(status().isForbidden());

        verify(postcodeService, never()).findPostcodeByPostcode(any());
    }

    // Tests for calculatePostcodeDistance
    @Test
    @WithMockUser(authorities = "CALCULATE_DISTANCE")
    void testCalculatePostcodeDistance_Success() throws Exception {
        when(postcodeService.calculateDistance(eq("12345"), eq("67890"))).thenReturn(calculateDistanceResponseDto);

        mockMvc.perform(post("/api/v1/admin/postcode/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calculateDistanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Distance calculated successfully"))
                .andExpect(jsonPath("$.result.distance").value(100.0));

        verify(postcodeService, times(1)).calculateDistance(eq("12345"), eq("67890"));
    }

    @Test
    @WithMockUser(authorities = "CALCULATE_DISTANCE")
    void testCalculatePostcodeDistance_Failure_NullInput() throws Exception {
        mockMvc.perform(post("/api/v1/admin/postcode/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(postcodeService, never()).calculateDistance(any(), any());
    }

    @Test
    @WithMockUser(authorities = "CALCULATE_DISTANCE")
    void testCalculatePostcodeDistance_Failure_CannotCalculate() throws Exception {
        when(postcodeService.calculateDistance(eq("12345"), eq("67890"))).thenReturn(null);

        mockMvc.perform(post("/api/v1/admin/postcode/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calculateDistanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Unable to calculate the distance"))
                .andExpect(jsonPath("$.result").isEmpty());

        verify(postcodeService, times(1)).calculateDistance(eq("12345"), eq("67890"));
    }

    @Test
    @WithMockUser(authorities = "VIEW_POSTAL")
    void testCalculatePostcodeDistance_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/v1/admin/postcode/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calculateDistanceDto)))
                .andExpect(status().isForbidden());

        verify(postcodeService, never()).calculateDistance(any(), any());
    }

}