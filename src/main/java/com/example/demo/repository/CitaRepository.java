package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByMedicoId(Long medicoId);

    List<Cita> findByMedicoIdOrderByFechaAscHoraAsc(Long medicoId);
    
    /**
     * Busca citas por médico y fecha específica
     */
    List<Cita> findByMedicoIdAndFecha(Long medicoId, java.time.LocalDate fecha);
    
    /**
     * Busca si existe una cita en un horario específico
     */
    boolean existsByMedicoIdAndFechaAndHora(Long medicoId, java.time.LocalDate fecha, java.time.LocalTime hora);

    @Modifying
    @Transactional
    void deleteByPacienteId(Long pacienteId);
}
