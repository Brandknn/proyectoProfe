package com.example.demo.model;

import java.time.LocalTime;

/**
 * DTO (Data Transfer Object) para representar un slot de tiempo disponible
 * Usado para mostrar al médico la disponibilidad de su agenda
 */
public class SlotDisponibilidad {
    
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible;
    private String nombrePaciente;
    private Long citaId;

    public SlotDisponibilidad(LocalTime horaInicio, LocalTime horaFin, boolean disponible) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
    }

    public SlotDisponibilidad(LocalTime horaInicio, LocalTime horaFin, boolean disponible, 
                              String nombrePaciente, Long citaId) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
        this.nombrePaciente = nombrePaciente;
        this.citaId = citaId;
    }

    // Getters y Setters
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    /**
     * Retorna la hora en formato legible (ej: "08:00 - 08:30")
     */
    public String getRangoHora() {
        return horaInicio.toString() + " - " + horaFin.toString();
    }
}
