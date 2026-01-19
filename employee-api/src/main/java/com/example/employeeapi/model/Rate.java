package com.example.employeeapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "oceny")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"grupa"})
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Wartość oceny nie może być pusta")
    @Min(value = 0, message = "Ocena nie może być mniejsza niż 0")
    @Max(value = 6, message = "Ocena nie może być większa niż 6")
    @Column(name = "wartosc_oceny", nullable = false)
    private int wartoscOceny;

    @ManyToOne(fetch = FetchType.LAZY)
    // W Rate.java

    @JoinColumn(name = "grupa_id", nullable = false)
    @JsonBackReference(value="grupa-rate")
// @NotNull(message = "Grupa musi być przypisana do oceny") // <-- Rozważ usunięcie tej linii, jeśli serwis zawsze ustawia grupę
    private GrupaPracownicza grupa;

    @Column(name = "data_wystawienia", nullable = false, updatable = false)
    @CreationTimestamp // Automatycznie ustawia datę przy tworzeniu
    private LocalDate dataWystawienia;

    @Column(columnDefinition = "TEXT")
    private String komentarz; // Opcjonalny
}

