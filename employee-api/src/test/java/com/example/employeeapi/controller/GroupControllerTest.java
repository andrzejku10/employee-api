package com.example.employeeapi.controller;

import com.example.employeeapi.model.GrupaPracownicza;
import com.example.employeeapi.model.Pracownik;
import com.example.employeeapi.service.GrupaPracowniczaService;
import com.example.employeeapi.service.PracownikService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrupaPracowniczaService grupaService;

    @MockBean
    private PracownikService pracownikService; // Również mockowany, bo jest wstrzykiwany do GroupController

    @Autowired
    private ObjectMapper objectMapper;

    private GrupaPracownicza grupa1;
    private GrupaPracownicza grupaDoWysłania;

    @BeforeEach
    void setUp() {
        grupa1 = new GrupaPracownicza(1L, "Programiści Java", Collections.emptyList(), Collections.emptyList());
        grupaDoWysłania = new GrupaPracownicza(null, "Testerzy", Collections.emptyList(), Collections.emptyList());
    }

    @Test
    void gdyGetWszystkieGrupy_toZwrocListeGrupIStatusOk() throws Exception {
        List<GrupaPracownicza> grupy = Arrays.asList(grupa1, new GrupaPracownicza(2L, "Analitycy", null, null));
        given(grupaService.znajdzWszystkieGrupy()).willReturn(grupy);

        mockMvc.perform(get("/api/group"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nazwa", is("Programiści Java")));
    }

    @Test
    void gdyDodajGrupe_poprawneDane_toZwrocStatusCreatedIGrupe() throws Exception {
        given(grupaService.dodajGrupe(any(GrupaPracownicza.class))).willReturn(new GrupaPracownicza(3L, "Testerzy", null, null));

        mockMvc.perform(post("/api/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grupaDoWysłania)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nazwa", is("Testerzy")));
    }

    @Test
    void gdyDodajGrupe_nazwaJuzIstnieje_toZwrocStatusConflict() throws Exception {
        given(grupaService.dodajGrupe(any(GrupaPracownicza.class)))
                .willThrow(new IllegalArgumentException("Grupa o nazwie 'Testerzy' już istnieje."));

        mockMvc.perform(post("/api/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grupaDoWysłania)))
                .andExpect(status().isBadRequest()) // IllegalArgumentException jest mapowany na 400 w GlobalExceptionHandler
                .andExpect(jsonPath("$.message", is("Grupa o nazwie 'Testerzy' już istnieje.")));
    }


    @Test
    void gdyUsunGrupe_istniejacyId_toZwrocStatusNoContent() throws Exception {
        Long grupaId = 1L;
        doNothing().when(grupaService).usunGrupe(grupaId);

        mockMvc.perform(delete("/api/group/{id}", grupaId))
                .andExpect(status().isNoContent());
    }

    @Test
    void gdyUsunGrupe_grupaZawieraPracownikow_toZwrocStatusConflict() throws Exception {
        Long grupaId = 1L;
        doThrow(new IllegalStateException("Nie można usunąć grupy, która zawiera pracowników."))
                .when(grupaService).usunGrupe(grupaId);

        mockMvc.perform(delete("/api/group/{id}", grupaId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Nie można usunąć grupy, która zawiera pracowników.")));
    }


    @Test
    void gdyGetPracownicyWGrupier_istniejacaGrupa_toZwrocListePracownikowIStatusOk() throws Exception {
        Long grupaId = 1L;
        List<Pracownik> pracownicy = Arrays.asList(new Pracownik(1L, "Jan", "Kowalski", 0, null, null, null));
        given(pracownikService.znajdzPracownikowWgGrupy(grupaId)).willReturn(pracownicy);

        mockMvc.perform(get("/api/group/{id}/employee", grupaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].imie", is("Jan")));
    }

    @Test
    void gdyGetPracownicyWGrupier_nieistniejacaGrupa_toZwrocStatusNotFound() throws Exception {
        Long nieistniejacyGrupaId = 99L;
        given(pracownikService.znajdzPracownikowWgGrupy(nieistniejacyGrupaId))
                .willThrow(new EntityNotFoundException("Nie znaleziono grupy o ID: " + nieistniejacyGrupaId));

        mockMvc.perform(get("/api/group/{id}/employee", nieistniejacyGrupaId))
                .andExpect(status().isNotFound());
    }


    @Test
    void gdyGetZapelnienieGrupy_istniejacaGrupa_toZwrocZapelnienieIStatusOk() throws Exception {
        Long grupaId = 1L;
        double zapelnienie = 60.0;
        given(grupaService.getZapelnienieProcentowe(grupaId)).willReturn(zapelnienie);

        mockMvc.perform(get("/api/group/{id}/fill", grupaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zapelnienieProcentowe", is(zapelnienie)));
    }
}