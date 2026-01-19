package com.example.employeeapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonBackReference; // Dla relacji dwukierunkowej

@Entity
@Table(name = "pracownicy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"grupaPracownicza"})
public class Pracownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    @Column(nullable = false)
    private String imie;

    @NotBlank(message = "Nazwisko nie może być puste")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    @Column(nullable = false)
    private String nazwisko;

    @NotNull(message = "Wynagrodzenie nie może być puste")
    @DecimalMin(value = "0.0", inclusive = false, message = "Wynagrodzenie musi być większe niż 0")
    private double wynagrodzenie;

    @NotNull(message = "Stan pracownika nie może być pusty")
    @Enumerated(EnumType.STRING)
    @Column(name = "stan_pracownika", nullable = false)
    private StanPracownika stan;

    private String choroba; // Opcjonalne, walidacja może być w serwisie jeśli stan=CHORY

    @ManyToOne(fetch = FetchType.LAZY) // LAZY jest często lepsze dla wydajności w API
    @JoinColumn(name = "grupa_id")
    @JsonBackReference // Zapobiega pętli w serializacji JSON
    private GrupaPracownicza grupaPracownicza;

    public enum StanPracownika {
        OBECNY("Obecny"),
        NIEOBECNY("Nieobecny"),
        CHORY("Chory"),
        DELEGACJA("Delegacja");

        private final String displayName;
        StanPracownika(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}