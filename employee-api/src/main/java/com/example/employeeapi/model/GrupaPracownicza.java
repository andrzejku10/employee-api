package com.example.employeeapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Dla relacji dwukierunkowej

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupy_pracownicze")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"pracownicy", "oceny"})
public class GrupaPracownicza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa grupy nie może być pusta")
    @Size(min = 2, max = 100, message = "Nazwa grupy musi mieć od 2 do 100 znaków")
    @Column(nullable = false, unique = true)
    private String nazwa;

    @OneToMany(mappedBy = "grupaPracownicza", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Zapobiega pętli w serializacji JSON
    private List<Pracownik> pracownicy = new ArrayList<>();

    @OneToMany(mappedBy = "grupa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @JsonManagedReference // Jeśli Rate miałby referencję zwrotną do Ocen
    private List<Rate> oceny = new ArrayList<>();
}