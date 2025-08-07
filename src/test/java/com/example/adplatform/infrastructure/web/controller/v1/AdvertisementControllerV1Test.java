package com.example.adplatform.infrastructure.web.controller.v1;

import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import com.example.adplatform.infrastructure.web.dto.AdvertisementDTO;
import com.example.adplatform.infrastructure.web.mapper.AdvertisementMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdvertisementControllerV1.class)
public class AdvertisementControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertisementService advertisementService;

    @MockBean
    private AdvertisementMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Advertisement testAd;
    private AdvertisementDTO testAdDto;
    private List<Advertisement> testAds;
    private List<AdvertisementDTO> testAdDtos;

    @BeforeEach
    void setUp() {
        testAd = Advertisement.builder()
                .id(1L)
                .title("Test Advertisement")
                .description("Test Description")
                .content("Test Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("test.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAdDto = new AdvertisementDTO();
        testAdDto.setId(1L);
        testAdDto.setTitle("Test Advertisement");
        testAdDto.setDescription("Test Description");
        testAdDto.setContent("Test Content");
        testAdDto.setSource(AdvertisementSource.STORAGE);
        testAdDto.setSourceIdentifier("test.mp4");
        testAdDto.setActive(true);

        testAds = Collections.singletonList(testAd);
        testAdDtos = Collections.singletonList(testAdDto);

        when(mapper.toDto(testAd)).thenReturn(testAdDto);
        when(mapper.toDomain(testAdDto)).thenReturn(testAd);
    }

    @Test
    void getAllAdvertisements_ShouldReturnAllAds() throws Exception {
        when(advertisementService.getAllAdvertisements()).thenReturn(testAds);

        mockMvc.perform(get("/api/v1/advertisements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Advertisement")));
    }

    @Test
    void getActiveAdvertisements_ShouldReturnActiveAds() throws Exception {
        when(advertisementService.getActiveAdvertisements()).thenReturn(testAds);

        mockMvc.perform(get("/api/v1/advertisements/active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].active", is(true)));
    }

    @Test
    void getAdvertisementById_ShouldReturnAd() throws Exception {
        when(advertisementService.getAdvertisementById(1L)).thenReturn(Optional.of(testAd));

        mockMvc.perform(get("/api/v1/advertisements/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Advertisement")));
    }

    @Test
    void getAdvertisementById_NotFound_ShouldReturn404() throws Exception {
        when(advertisementService.getAdvertisementById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/advertisements/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAdvertisement_ShouldReturnCreatedAd() throws Exception {
        AdvertisementDTO newAdDto = new AdvertisementDTO();
        newAdDto.setTitle("New Advertisement");
        newAdDto.setDescription("New Description");
        newAdDto.setContent("New Content");
        newAdDto.setSource(AdvertisementSource.STORAGE);
        newAdDto.setSourceIdentifier("new.mp4");
        newAdDto.setActive(true);

        Advertisement newAd = Advertisement.builder()
                .title("New Advertisement")
                .description("New Description")
                .content("New Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("new.mp4")
                .active(true)
                .build();

        Advertisement savedAd = Advertisement.builder()
                .id(2L)
                .title("New Advertisement")
                .description("New Description")
                .content("New Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("new.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AdvertisementDTO savedAdDto = new AdvertisementDTO();
        savedAdDto.setId(2L);
        savedAdDto.setTitle("New Advertisement");
        savedAdDto.setDescription("New Description");
        savedAdDto.setContent("New Content");
        savedAdDto.setSource(AdvertisementSource.STORAGE);
        savedAdDto.setSourceIdentifier("new.mp4");
        savedAdDto.setActive(true);

        when(mapper.toDomain(newAdDto)).thenReturn(newAd);
        when(advertisementService.saveAdvertisement(any(Advertisement.class))).thenReturn(savedAd);
        when(mapper.toDto(savedAd)).thenReturn(savedAdDto);

        mockMvc.perform(post("/api/v1/advertisements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAdDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("New Advertisement")));
    }

    @Test
    void contentNegotiationVersioning_ShouldWork() throws Exception {
        when(advertisementService.getAllAdvertisements()).thenReturn(testAds);

        mockMvc.perform(get("/api/v1/advertisements")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Advertisement")));
    }
}