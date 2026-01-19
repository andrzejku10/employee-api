package com.example.employeeapi.controller;

import com.example.employeeapi.model.Pracownik;
import com.example.employeeapi.model.Pracownik.StanPracownika;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;


@WebMvcTest(EmployeeController.class) // Testujemy tylko warstwę kontrolera EmployeeController
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc; // Do wykonywania żądań HTTP

    @MockBean // Spring dostarczy mocka tego serwisu, nie będzie używana prawdziwa implementacja
    private PracownikService pracownikService;

    @Autowired
    private ObjectMapper objectMapper; // Do konwersji obiektów Java na JSON i odwrotnie

    private Pracownik pracownik1;
    private Pracownik pracownikDoWysłania;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych używanych w wielu testach
        pracownik1 = new Pracownik(1L, "Jan", "Kowalski", 5000.0, StanPracownika.OBECNY, null, null);
        pracownikDoWysłania = new Pracownik(null, "Anna", "Nowak", 6000.0, StanPracownika.DELEGACJA, "Szkolenie", null);
    }

    @Test
    void gdyDodajPracownika_poprawneDane_toZwrocStatusCreatedIPracownika() throws Exception {
        Long grupaId = 1L;
        // Symulujemy, że serwis poprawnie zapisze pracownika i zwróci go z nadanym ID
        given(pracownikService.dodajPracownika(any(Pracownik.class), eq(grupaId))).willReturn(new Pracownik(2L, "Anna", "Nowak", 6000.0, StanPracownika.DELEGACJA, "Szkolenie", null));

        mockMvc.perform(post("/api/employee")
                        .param("grupaId", grupaId.toString()) // Parametr żądania
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pracownikDoWysłania))) // Ciało żądania jako JSON
                .andExpect(status().isCreated()) // Oczekujemy statusu 201 Created
                .andExpect(jsonPath("$.id", is(2))) // Sprawdzamy pole 'id' w odpowiedzi JSON
                .andExpect(jsonPath("$.imie", is("Anna"))); // Sprawdzamy pole 'imie'
    }

    @Test
    void gdyDodajPracownika_pelnaGrupa_toZwrocStatusConflict() throws Exception {
        Long grupaId = 1L;
        given(pracownikService.dodajPracownika(any(Pracownik.class), eq(grupaId)))
                .willThrow(new IllegalStateException("Grupa jest pełna.")); // Serwis rzuca wyjątek

        mockMvc.perform(post("/api/employee")
                        .param("grupaId", grupaId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pracownikDoWysłania)))
                .andExpect(status().isConflict()) // GlobalExceptionHandler powinien to obsłużyć
                .andExpect(jsonPath("$.message", is("Grupa jest pełna.")));
    }


    @Test
    void gdyUsunPracownika_istniejacyId_toZwrocStatusNoContent() throws Exception {
        Long pracownikId = 1L;
        // Symulujemy, że serwis nic nie robi przy usuwaniu (void)
        doNothing().when(pracownikService).usunPracownika(pracownikId);

        mockMvc.perform(delete("/api/employee/{id}", pracownikId))
                .andExpect(status().isNoContent()); // Oczekujemy statusu 204 No Content
    }

    @Test
    void gdyUsunPracownika_nieistniejacyId_toZwrocStatusNotFound() throws Exception {
        Long nieistniejacyId = 99L;
        // Symulujemy, że serwis rzuca wyjątek, gdy pracownik nie istnieje
        doThrow(new EntityNotFoundException("Nie znaleziono pracownika o ID: " + nieistniejacyId + " do usunięcia."))
                .when(pracownikService).usunPracownika(nieistniejacyId);

        mockMvc.perform(delete("/api/employee/{id}", nieistniejacyId))
                .andExpect(status().isNotFound()) // GlobalExceptionHandler powinien to obsłużyć
                .andExpect(jsonPath("$.message", containsString("Nie znaleziono pracownika o ID: " + nieistniejacyId)));
    }

    @Test
    void gdyGetPracownicyCsv_toZwrocCsvDataIStatusOk() throws Exception {
        String oczekiwaneCsv = "ID;Imie;Nazwisko;Wynagrodzenie;Stan;Choroba;GrupaID;NazwaGrupy\n1;Jan;Kowalski;5000.00;OBECNY;;;";
        // Symulujemy, że serwis zwraca dane CSV
        given(pracownikService.generujCsvWszystkichPracownikow()).willReturn(oczekiwaneCsv);

        mockMvc.perform(get("/api/employee/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"pracownicy.csv\""))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)) // Użyj compatibleWith dla elastyczności
                .andExpect(content().string(oczekiwaneCsv));
    }
}