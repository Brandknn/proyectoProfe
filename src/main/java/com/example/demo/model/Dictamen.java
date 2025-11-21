package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dictamen")
public class Dictamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(name = "cita_id", nullable = false, unique = true)
    private Long citaId;

    public Dictamen() {}

    public Dictamen(String contenido, Long citaId) {
        this.contenido = contenido;
        this.citaId = citaId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }
}