package com.example.employeeapi.service;

import com.example.employeeapi.model.Pracownik;
import java.util.List;
import java.util.Optional;

public interface PracownikService {
    Pracownik dodajPracownika(Pracownik pracownik, Long grupaId);
    Optional<Pracownik> znajdzPracownika(Long id);
    List<Pracownik> znajdzWszystkichPracownikow();
    void usunPracownika(Long id);
    // Pracownik aktualizujPracownika(Long id, Pracownik pracownikDetails); // Można dodać jeśli potrzebne
    List<Pracownik> znajdzPracownikowWgGrupy(Long grupaId);
    String generujCsvWszystkichPracownikow();
}