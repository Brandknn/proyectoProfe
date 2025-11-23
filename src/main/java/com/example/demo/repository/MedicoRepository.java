package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByEmailAndPassword(String email, String password);

    Optional<Medico> findByEmail(String email);

    Optional<Medico> findByGoogleId(String googleId);

    Optional<Medico> findByCedula(String cedula);

    Optional<Medico> findByTelefono(String telefono);
}