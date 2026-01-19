package com.example.employeeapi.repository;

import com.example.employeeapi.model.Pracownik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracownikRepository extends JpaRepository<Pracownik, Long> {
    List<Pracownik> findByGrupaPracowniczaId(Long grupaId);
    List<Pracownik> findByNazwiskoContainingIgnoreCase(String fragmentNazwiska);
}