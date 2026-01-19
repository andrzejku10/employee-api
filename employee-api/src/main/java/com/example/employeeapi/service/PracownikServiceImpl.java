package com.example.employeeapi.service;

import com.example.employeeapi.model.GrupaPracownicza;
import com.example.employeeapi.model.Pracownik;
import com.example.employeeapi.repository.GrupaPracowniczaRepository;
import com.example.employeeapi.repository.PracownikRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PracownikServiceImpl implements PracownikService {

    private final PracownikRepository pracownikRepository;
    private final GrupaPracowniczaRepository grupaRepository;
    private static final int MAX_PRACOWNIKOW_W_GRUPIE = 5; // Zgodnie z zadaniem 4

    @Autowired
    public PracownikServiceImpl(PracownikRepository pracownikRepository, GrupaPracowniczaRepository grupaRepository) {
        this.pracownikRepository = pracownikRepository;
        this.grupaRepository = grupaRepository;
    }

    @Override
    public Pracownik dodajPracownika(Pracownik pracownik, Long grupaId) {
        GrupaPracownicza grupa = grupaRepository.findById(grupaId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + grupaId));

        if (pracownikRepository.findByGrupaPracowniczaId(grupaId).size() >= MAX_PRACOWNIKOW_W_GRUPIE) {
            throw new IllegalStateException("Grupa '" + grupa.getNazwa() + "' jest pełna. Nie można dodać pracownika.");
        }

        pracownik.setGrupaPracownicza(grupa);
        return pracownikRepository.save(pracownik);
    }

    @Override
    public Optional<Pracownik> znajdzPracownika(Long id) {
        return pracownikRepository.findById(id);
    }

    @Override
    public List<Pracownik> znajdzWszystkichPracownikow() {
        return pracownikRepository.findAll();
    }

    @Override
    public void usunPracownika(Long id) {
        if (!pracownikRepository.existsById(id)) {
            throw new EntityNotFoundException("Nie znaleziono pracownika o ID: " + id + " do usunięcia.");
        }
        pracownikRepository.deleteById(id);
    }

    @Override
    public List<Pracownik> znajdzPracownikowWgGrupy(Long grupaId) {
        if (!grupaRepository.existsById(grupaId)) {
            throw new EntityNotFoundException("Nie znaleziono grupy o ID: " + grupaId);
        }
        return pracownikRepository.findByGrupaPracowniczaId(grupaId);
    }

    @Override
    public String generujCsvWszystkichPracownikow() {
        List<Pracownik> pracownicy = pracownikRepository.findAll();
        if (pracownicy.isEmpty()) {
            return "";
        }
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            pw.println("ID;Imie;Nazwisko;Wynagrodzenie;Stan;Choroba;GrupaID;NazwaGrupy");
            for (Pracownik p : pracownicy) {
                pw.printf("%d;%s;%s;%.2f;%s;%s;%s;%s\n",
                        p.getId(),
                        p.getImie(),
                        p.getNazwisko(),
                        p.getWynagrodzenie(),
                        p.getStan().name(),
                        p.getChoroba() != null ? p.getChoroba() : "",
                        p.getGrupaPracownicza() != null ? p.getGrupaPracownicza().getId().toString() : "",
                        p.getGrupaPracownicza() != null ? p.getGrupaPracownicza().getNazwa() : ""
                );
            }
        }
        return sw.toString();
    }
}