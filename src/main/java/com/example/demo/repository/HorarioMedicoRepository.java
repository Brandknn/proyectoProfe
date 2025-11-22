package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.HorarioMedico;

@Repository
public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {
    
    /**
     * Busca todos los horarios de un médico específico
     * @param medicoId ID del médico
     * @return Lista de horarios configurados
     */
    List<HorarioMedico> findByMedicoId(Long medicoId);
    
    /**
     * Busca el horario de un médico para un día específico de la semana
     * @param medicoId ID del médico
     * @param diaSemana Día de la semana (1-7)
     * @return El horario si existe
     */
    Optional<HorarioMedico> findByMedicoIdAndDiaSemana(Long medicoId, Integer diaSemana);
    
    /**
     * Busca si existe horario activopara un médico y día
     * @param medicoId ID del médico
     * @param diaSemana Día de la semana (1-7)
     * @param activo Estado del horario
     * @return El horario si existe y está activo
     */
    Optional<HorarioMedico> findByMedicoIdAndDiaSemanaAndActivo(Long medicoId, Integer diaSemana, Boolean activo);
}
