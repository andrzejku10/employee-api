package com.example.employeeapi.controller;

import com.example.employeeapi.model.Pracownik;
import com.example.employeeapi.service.PracownikService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException; // Już niepotrzebne, obsługa w GlobalExceptionHandler

// import java.util.List; // Jeśli dodasz endpoint GET /api/employee

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final PracownikService pracownikService;

    @Autowired
    public EmployeeController(PracownikService pracownikService) {
        this.pracownikService = pracownikService;
    }

    @PostMapping
    public ResponseEntity<Pracownik> dodajPracownika(@Valid @RequestBody Pracownik pracownik,
                                                     @RequestParam Long grupaId) {
        Pracownik nowyPracownik = pracownikService.dodajPracownika(pracownik, grupaId);
        return new ResponseEntity<>(nowyPracownik, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> usunPracownika(@PathVariable Long id) {
        pracownikService.usunPracownika(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csv")
    public ResponseEntity<String> getPracownicyCsv() {
        String csvData = pracownikService.generujCsvWszystkichPracownikow();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN); // lub "text/csv"
        headers.setContentDispositionFormData("attachment", "pracownicy.csv");
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}