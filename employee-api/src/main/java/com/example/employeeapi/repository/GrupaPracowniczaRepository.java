package com.example.employeeapi.repository;

import com.example.employeeapi.model.GrupaPracownicza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupaPracowniczaRepository extends JpaRepository<GrupaPracownicza, Long> {
    Optional<GrupaPracownicza> findByNazwa(String nazwa);
}