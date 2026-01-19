package com.example.employeeapi.service;

import com.example.employeeapi.model.Rate;
import java.util.List;

public interface RateService {
    Rate dodajOcene(Rate rate, Long grupaId);
    // List<Object[]> getRateStatsByGroup(); // Można dodać, jeśli potrzebne
}