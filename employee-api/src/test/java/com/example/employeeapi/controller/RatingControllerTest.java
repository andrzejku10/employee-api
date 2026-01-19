package com.example.employeeapi.controller;

import com.example.employeeapi.model.Rate;
import com.example.employeeapi.service.RateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateService rateService;

    @Autowired
    private ObjectMapper objectMapper;

    private Rate rateDoWysłania;

    @BeforeEach
    void setUp() {
        // Grupa będzie mockowana w serwisie, więc tu nie jest potrzebna pełna instancja
        rateDoWysłania = new Rate(null, 5, null, null, "Dobry komentarz");
    }

    @Test
    void gdyDodajOcene_poprawneDane_toZwrocStatusCreatedIOcene() throws Exception {
        Long grupaId = 1L;
        // Symulujemy, że serwis zwróci ocenę z nadanym ID i datą
        Rate zapisanaOcena = new Rate(1L, 5, null, java.time.LocalDate.now(), "Dobry komentarz");
        given(rateService.dodajOcene(any(Rate.class), eq(grupaId))).willReturn(zapisanaOcena);

        mockMvc.perform(post("/api/rating")
                        .param("grupaId", grupaId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateDoWysłania)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.wartoscOceny", is(5)));
    }

    @Test
    void gdyDodajOcene_nieistniejacaGrupa_toZwrocStatusNotFound() throws Exception {
        Long nieistniejacyGrupaId = 99L;
        given(rateService.dodajOcene(any(Rate.class), eq(nieistniejacyGrupaId)))
                .willThrow(new EntityNotFoundException("Nie znaleziono grupy o ID: " + nieistniejacyGrupaId));

        mockMvc.perform(post("/api/rating")
                        .param("grupaId", nieistniejacyGrupaId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateDoWysłania)))
                .andExpect(status().isNotFound());
    }

    @Test
    void gdyDodajOcene_niepoprawnaWartoscOceny_toZwrocStatusBadRequest() throws Exception {
        Long grupaId = 1L;
        Rate rateZNieprawidlowaOcena = new Rate(null, 7, null, null, "Zła ocena"); // Ocena > 6


        given(rateService.dodajOcene(any(Rate.class), eq(grupaId)))
                .willThrow(new IllegalArgumentException("Wartość oceny musi być w przedziale 0-6."));

        mockMvc.perform(post("/api/rating")
                        .param("grupaId", grupaId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateZNieprawidlowaOcena)))
                .andExpect(status().isBadRequest());
    }
}