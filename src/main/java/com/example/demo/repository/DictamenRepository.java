package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Dictamen;

@Repository
public interface DictamenRepository extends JpaRepository<Dictamen, Long> {
    Optional<Dictamen> findByCitaId(Long citaId);

    // NUEVO: Obtener todos los dictámenes con información de la cita
    @Query("SELECT d FROM Dictamen d ORDER BY d.id DESC")
    List<Dictamen> findAllOrderByIdDesc();
}