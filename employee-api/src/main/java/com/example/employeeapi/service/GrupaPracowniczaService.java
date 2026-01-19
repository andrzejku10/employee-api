package com.example.employeeapi.service;

import com.example.employeeapi.model.GrupaPracownicza;
import java.util.List;
import java.util.Optional;

public interface GrupaPracowniczaService {
    GrupaPracownicza dodajGrupe(GrupaPracownicza grupa);
    Optional<GrupaPracownicza> znajdzGrupe(Long id);
    List<GrupaPracownicza> znajdzWszystkieGrupy();
    void usunGrupe(Long id);
    // GrupaPracownicza aktualizujGrupe(Long id, GrupaPracownicza grupaDetails); // Można dodać
    double getZapelnienieProcentowe(Long grupaId);
}