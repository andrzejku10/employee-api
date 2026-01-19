package com.example.employeeapi.controller;

import com.example.employeeapi.model.Rate;
import com.example.employeeapi.service.RateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/rating") // Zmieniono bazowy mapping dla spójności
public class RatingController {

    private final RateService rateService;

    @Autowired
    public RatingController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping
    public ResponseEntity<Rate> dodajOcene(@Valid @RequestBody Rate rate, @RequestParam Long grupaId) {
        Rate nowaOcena = rateService.dodajOcene(rate, grupaId);
        return new ResponseEntity<>(nowaOcena, HttpStatus.CREATED);
    }
}