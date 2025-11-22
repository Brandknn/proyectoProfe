package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaConPaciente {
    private Long id;
    private String pacienteNombre;
    private Long pacienteId;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String estado;

    public CitaConPaciente(Cita cita, String pacienteNombre) {
        this.id = cita.getId();
        this.pacienteId = cita.getPacienteId();
        this.pacienteNombre = pacienteNombre;
        this.fecha = cita.getFecha();
        this.hora = cita.getHora();
        this.motivo = cita.getMotivo();
        this.estado = cita.getEstado().getDescripcion(); // Convertir enum a String
    }

    public Long getId() {
        return id;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getEstado() {
        return estado;
    }
}
