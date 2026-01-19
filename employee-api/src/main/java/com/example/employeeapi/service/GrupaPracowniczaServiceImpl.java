package com.example.employeeapi.service;

import com.example.employeeapi.model.GrupaPracownicza;
import com.example.employeeapi.repository.GrupaPracowniczaRepository;
import com.example.employeeapi.repository.PracownikRepository; // Potrzebne do getZapelnienieProcentowe
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrupaPracowniczaServiceImpl implements GrupaPracowniczaService {

    private final GrupaPracowniczaRepository grupaRepository;
    private final PracownikRepository pracownikRepository;
    private static final int MAX_PRACOWNIKOW_W_GRUPIE = 5;

    @Autowired
    public GrupaPracowniczaServiceImpl(GrupaPracowniczaRepository grupaRepository, PracownikRepository pracownikRepository) {
        this.grupaRepository = grupaRepository;
        this.pracownikRepository = pracownikRepository;
    }

    @Override
    public GrupaPracownicza dodajGrupe(GrupaPracownicza grupa) {
        if (grupaRepository.findByNazwa(grupa.getNazwa()).isPresent()) {
            throw new IllegalArgumentException("Grupa o nazwie '" + grupa.getNazwa() + "' już istnieje.");
        }
        return grupaRepository.save(grupa);
    }

    @Override
    public Optional<GrupaPracownicza> znajdzGrupe(Long id) {
        return grupaRepository.findById(id);
    }

    @Override
    public List<GrupaPracownicza> znajdzWszystkieGrupy() {
        return grupaRepository.findAll();
    }

    @Override
    public void usunGrupe(Long id) {
        GrupaPracownicza grupa = grupaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + id + " do usunięcia."));

        if (!pracownikRepository.findByGrupaPracowniczaId(id).isEmpty()) {
            throw new IllegalStateException("Nie można usunąć grupy, która zawiera pracowników. Najpierw usuń pracowników.");
        }
        grupaRepository.delete(grupa);
    }

    @Override
    public double getZapelnienieProcentowe(Long grupaId) {
        if (!grupaRepository.existsById(grupaId)) {
            throw new EntityNotFoundException("Nie znaleziono grupy o ID: " + grupaId);
        }
        long liczbaPracownikow = pracownikRepository.findByGrupaPracowniczaId(grupaId).size();
        return (MAX_PRACOWNIKOW_W_GRUPIE == 0) ? 0.0 : ((double) liczbaPracownikow / MAX_PRACOWNIKOW_W_GRUPIE) * 100.0;
    }
}