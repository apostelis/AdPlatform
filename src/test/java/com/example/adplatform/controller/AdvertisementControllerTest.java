package com.example.adplatform.controller;

import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import com.example.adplatform.infrastructure.web.controller.v1.AdvertisementControllerV1;
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
public class AdvertisementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertisementService advertisementService;
    
    @MockBean
    private AdvertisementMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Advertisement testAd;
    private List<Advertisement> testAds;

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

        Advertisement ad2 = Advertisement.builder()
                .id(2L)
                .title("Another Ad")
                .description("Another Description")
                .content("Another Content")
                .source(AdvertisementSource.YOUTUBE)
                .sourceIdentifier("youtube123")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAds = Arrays.asList(testAd, ad2);
        
        // Setup mapper mocking
        when(mapper.toDto(any(Advertisement.class))).thenAnswer(invocation -> {
            Advertisement ad = invocation.getArgument(0);
            com.example.adplatform.infrastructure.web.dto.AdvertisementDTO dto = 
                new com.example.adplatform.infrastructure.web.dto.AdvertisementDTO();
            dto.setId(ad.getId());
            dto.setTitle(ad.getTitle());
            dto.setDescription(ad.getDescription());
            dto.setContent(ad.getContent());
            dto.setSource(ad.getSource());
            dto.setSourceIdentifier(ad.getSourceIdentifier());
            dto.setActive(ad.isActive());
            return dto;
        });
        
        when(mapper.toDomain(any(com.example.adplatform.infrastructure.web.dto.AdvertisementDTO.class)))
            .thenAnswer(invocation -> {
                com.example.adplatform.infrastructure.web.dto.AdvertisementDTO dto = invocation.getArgument(0);
                return Advertisement.builder()
                    .id(dto.getId())
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .content(dto.getContent())
                    .source(dto.getSource())
                    .sourceIdentifier(dto.getSourceIdentifier())
                    .active(dto.isActive())
                    .build();
            });
    }

    @Test
    void getAllAdvertisements_ShouldReturnAllAds() throws Exception {
        when(advertisementService.getAllAdvertisements()).thenReturn(testAds);

        mockMvc.perform(get("/api/v1/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Advertisement")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Another Ad")));
    }

    @Test
    void getActiveAdvertisements_ShouldReturnActiveAds() throws Exception {
        when(advertisementService.getActiveAdvertisements()).thenReturn(testAds);

        mockMvc.perform(get("/api/v1/advertisements/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].active", is(true)))
                .andExpect(jsonPath("$[1].active", is(true)));
    }

    @Test
    void getAdvertisementById_ShouldReturnAd() throws Exception {
        when(advertisementService.getAdvertisementById(1L)).thenReturn(Optional.of(testAd));

        mockMvc.perform(get("/api/v1/advertisements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Advertisement")));
    }

    @Test
    void getAdvertisementById_NotFound_ShouldReturn404() throws Exception {
        when(advertisementService.getAdvertisementById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/advertisements/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAdvertisement_ShouldReturnCreatedAd() throws Exception {
        com.example.adplatform.infrastructure.web.dto.AdvertisementDTO newAdDto = 
            new com.example.adplatform.infrastructure.web.dto.AdvertisementDTO();
        newAdDto.setTitle("New Ad");
        newAdDto.setDescription("New Description");
        newAdDto.setContent("New Content");
        newAdDto.setSource(AdvertisementSource.STORAGE);
        newAdDto.setSourceIdentifier("new.mp4");
        newAdDto.setActive(true);

        Advertisement newAd = Advertisement.builder()
                .title("New Ad")
                .description("New Description")
                .content("New Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("new.mp4")
                .active(true)
                .build();

        Advertisement savedAd = Advertisement.builder()
                .id(3L)
                .title("New Ad")
                .description("New Description")
                .content("New Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("new.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(mapper.toDomain(any(com.example.adplatform.infrastructure.web.dto.AdvertisementDTO.class))).thenReturn(newAd);
        when(advertisementService.saveAdvertisement(any(Advertisement.class))).thenReturn(savedAd);

        mockMvc.perform(post("/api/v1/advertisements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAdDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Ad")));
    }

    @Test
    void updateAdvertisement_ShouldReturnUpdatedAd() throws Exception {
        com.example.adplatform.infrastructure.web.dto.AdvertisementDTO updatedAdDto = 
            new com.example.adplatform.infrastructure.web.dto.AdvertisementDTO();
        updatedAdDto.setId(1L);
        updatedAdDto.setTitle("Updated Title");
        updatedAdDto.setDescription("Updated Description");
        updatedAdDto.setContent("Updated Content");
        updatedAdDto.setSource(AdvertisementSource.STORAGE);
        updatedAdDto.setSourceIdentifier("updated.mp4");
        updatedAdDto.setActive(true);

        Advertisement updatedAd = Advertisement.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("updated.mp4")
                .active(true)
                .build();

        when(advertisementService.getAdvertisementById(1L)).thenReturn(Optional.of(testAd));
        when(mapper.toDomain(any(com.example.adplatform.infrastructure.web.dto.AdvertisementDTO.class))).thenReturn(updatedAd);
        when(advertisementService.saveAdvertisement(any(Advertisement.class))).thenReturn(updatedAd);

        mockMvc.perform(put("/api/v1/advertisements/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAdDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")));
    }

    @Test
    void deleteAdvertisement_ShouldReturn204() throws Exception {
        when(advertisementService.getAdvertisementById(1L)).thenReturn(Optional.of(testAd));

        mockMvc.perform(delete("/api/v1/advertisements/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAdvertisementsBySource_ShouldReturnMatchingAds() throws Exception {
        when(advertisementService.getAdvertisementsBySource(AdvertisementSource.STORAGE))
                .thenReturn(Collections.singletonList(testAd));

        mockMvc.perform(get("/api/v1/advertisements/source/STORAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].source", is("STORAGE")));
    }

    @Test
    void getAdvertisementsByTitle_ShouldReturnMatchingAds() throws Exception {
        when(advertisementService.getAdvertisementsByTitle("Test"))
                .thenReturn(Collections.singletonList(testAd));

        mockMvc.perform(get("/api/v1/advertisements/search?title=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Advertisement")));
    }

    @Test
    void getTargetedAdvertisements_ShouldReturnMatchingAds() throws Exception {
        Map<String, Object> userBioData = new HashMap<>();
        userBioData.put("age", 30);
        userBioData.put("gender", "MALE");

        when(advertisementService.getTargetedAdvertisements(eq("US"), any(Map.class), eq(Mood.HAPPY)))
                .thenReturn(Collections.singletonList(testAd));

        mockMvc.perform(post("/api/v1/advertisements/targeted?countryCode=US&mood=HAPPY")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userBioData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getGeoTargetedAdvertisements_ShouldReturnMatchingAds() throws Exception {
        when(advertisementService.getGeoTargetedAdvertisements("US", "California", "San Francisco", null, null))
                .thenReturn(Collections.singletonList(testAd));

        mockMvc.perform(get("/api/v1/advertisements/geo-targeted?countryCode=US&region=California&city=San Francisco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getMoodTargetedAdvertisements_ShouldReturnMatchingAds() throws Exception {
        when(advertisementService.getMoodTargetedAdvertisements(Mood.HAPPY, 7, "Morning", null, null))
                .thenReturn(Collections.singletonList(testAd));

        mockMvc.perform(get("/api/v1/advertisements/mood-targeted?mood=HAPPY&intensity=7&timeOfDay=Morning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}