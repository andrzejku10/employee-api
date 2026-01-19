package com.example.employeeapi.service;

import com.example.employeeapi.model.GrupaPracownicza;
import com.example.employeeapi.model.Rate;
import com.example.employeeapi.repository.GrupaPracowniczaRepository;
import com.example.employeeapi.repository.RateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;
    private final GrupaPracowniczaRepository grupaRepository;

    @Autowired
    public RateServiceImpl(RateRepository rateRepository, GrupaPracowniczaRepository grupaRepository) {
        this.rateRepository = rateRepository;
        this.grupaRepository = grupaRepository;
    }

    @Override
    public Rate dodajOcene(Rate rate, Long grupaId) {
        GrupaPracownicza grupa = grupaRepository.findById(grupaId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + grupaId + " do dodania oceny."));
        rate.setGrupa(grupa);

        return rateRepository.save(rate);
    }
}