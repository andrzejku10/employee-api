package com.example.employeeapi.controller;

import com.example.employeeapi.model.GrupaPracownicza;
import com.example.employeeapi.model.Pracownik;
import com.example.employeeapi.service.GrupaPracowniczaService;
import com.example.employeeapi.service.PracownikService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GrupaPracowniczaService grupaService;
    private final PracownikService pracownikService;

    @Autowired
    public GroupController(GrupaPracowniczaService grupaService, PracownikService pracownikService) {
        this.grupaService = grupaService;
        this.pracownikService = pracownikService;
    }

    @GetMapping
    public ResponseEntity<List<GrupaPracownicza>> getWszystkieGrupy() {
        return ResponseEntity.ok(grupaService.znajdzWszystkieGrupy());
    }

    @PostMapping
    public ResponseEntity<GrupaPracownicza> dodajGrupe(@Valid @RequestBody GrupaPracownicza grupa) {
        GrupaPracownicza nowaGrupa = grupaService.dodajGrupe(grupa);
        return new ResponseEntity<>(nowaGrupa, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> usunGrupe(@PathVariable Long id) {
        grupaService.usunGrupe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/employee")
    public ResponseEntity<List<Pracownik>> getPracownicyWGrupier(@PathVariable Long id) {
        List<Pracownik> pracownicy = pracownikService.znajdzPracownikowWgGrupy(id);
        return ResponseEntity.ok(pracownicy);
    }

    @GetMapping("/{id}/fill")
    public ResponseEntity<Map<String, Double>> getZapelnienieGrupy(@PathVariable Long id) {
        double zapelnienie = grupaService.getZapelnienieProcentowe(id);
        return ResponseEntity.ok(Map.of("zapelnienieProcentowe", zapelnienie));
    }
}