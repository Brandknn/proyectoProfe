package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByMedicoId(Long medicoId);

    Paciente findByDocumento(String documento);

    Paciente findByCorreo(String correo);

    Paciente findByTelefono(Long telefono);
}
