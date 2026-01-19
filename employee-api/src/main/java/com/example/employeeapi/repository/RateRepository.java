package com.example.employeeapi.repository;

import com.example.employeeapi.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByGrupaId(Long grupaId);

    @Query("SELECT r.grupa.nazwa, COUNT(r), AVG(r.wartoscOceny) FROM Rate r GROUP BY r.grupa.nazwa")
    List<Object[]> getRateStatsByGroup();
}