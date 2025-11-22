package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Cita;
import com.example.demo.model.HorarioMedico;
import com.example.demo.model.SlotDisponibilidad;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.HorarioMedicoRepository;
import com.example.demo.repository.PacienteRepository;

/**
 * Servicio para gestionar horarios y disponibilidad de slots
 */
@Service
public class HorarioService {
    
    private final HorarioMedicoRepository horarioRepository;
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    
    public HorarioService(HorarioMedicoRepository horarioRepository, 
                         CitaRepository citaRepository,
                         PacienteRepository pacienteRepository) {
        this.horarioRepository = horarioRepository;
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
    }
    
    /**
     * Guarda la configuración de horario para un médico
     */
    public void guardarHorario(Long medicoId, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        // Buscar si ya existe horario para ese día
        Optional<HorarioMedico> existente = horarioRepository.findByMedicoIdAndDiaSemana(medicoId, diaSemana);
        
        if (existente.isPresent()) {
            HorarioMedico horario = existente.get();
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);
            horario.setActivo(true);
            horarioRepository.save(horario);
        } else {
            HorarioMedico nuevoHorario = new HorarioMedico(medicoId, diaSemana, horaInicio, horaFin);
            horarioRepository.save(nuevoHorario);
        }
    }
    
    /**
     * Obtiene todos los horarios de un médico
     */
    public List<HorarioMedico> obtenerHorariosMedico(Long medicoId) {
        return horarioRepository.findByMedicoId(medicoId);
    }
    
    /**
     * Obtiene el horario de un médico para un día específico
     */
    public Optional<HorarioMedico> obtenerHorarioDia(Long medicoId, Integer diaSemana) {
        return horarioRepository.findByMedicoIdAndDiaSemanaAndActivo(medicoId, diaSemana, true);
    }
    
    /**
     * Calcula todos los slots disponibles para una fecha específica
     * Cada slot es de 30 minutos
     * 
     * @param medicoId ID del médico
     * @param fecha Fecha para la cual se quieren ver los slots
     * @return Lista de slots con su disponibilidad
     */
    public List<SlotDisponibilidad> calcularSlotsDisponibles(Long medicoId, LocalDate fecha) {
        List<SlotDisponibilidad> slots = new ArrayList<>();
        
        // Obtener día de la semana (1=Lunes, 7=Domingo)
        int diaSemana = fecha.getDayOfWeek().getValue();
        
        // Obtener horario configurado para ese día
        Optional<HorarioMedico> horarioOpt = obtenerHorarioDia(medicoId, diaSemana);
        
        if (horarioOpt.isEmpty()) {
            return slots; // No hay horario configurado para ese día
        }
        
        HorarioMedico horario = horarioOpt.get();
        LocalTime horaActual = horario.getHoraInicio();
        LocalTime horaFin = horario.getHoraFin();
        
        // Obtener todas las citas del médico para esa fecha
        List<Cita> citasDelDia = citaRepository.findByMedicoIdAndFecha(medicoId, fecha);
        
        // Crear un mapa de horas ocupadas
        Map<LocalTime, Cita> horasOcupadas = new HashMap<>();
        for (Cita cita : citasDelDia) {
            horasOcupadas.put(cita.getHora(), cita);
        }
        
        // Generar slots de 30 minutos
        while (horaActual.isBefore(horaFin)) {
            LocalTime horaFinSlot = horaActual.plusMinutes(30);
            
            // Verificar si este slot está ocupado
            if (horasOcupadas.containsKey(horaActual)) {
                Cita cita = horasOcupadas.get(horaActual);
                String nombrePaciente = obtenerNombrePaciente(cita.getPacienteId());
                slots.add(new SlotDisponibilidad(horaActual, horaFinSlot, false, nombrePaciente, cita.getId()));
            } else {
                slots.add(new SlotDisponibilidad(horaActual, horaFinSlot, true));
            }
            
            horaActual = horaFinSlot;
        }
        
        return slots;
    }
    
    /**
     * Valida si se puede modificar el horario sin afectar citas existentes
     * 
     * @param medicoId ID del médico
     * @param diaSemana Día de la semana a modificar
     * @param nuevoInicio Nueva hora de inicio
     * @param nuevoFin Nueva hora de fin
     * @return true si se puede modificar, false si hay citas que quedarían fuera del nuevo rango
     */
    public boolean validarModificacionHorario(Long medicoId, Integer diaSemana, 
                                             LocalTime nuevoInicio, LocalTime nuevoFin) {
        // Obtener todas las citas futuras para este día de la semana
        List<Cita> todasCitas = citaRepository.findByMedicoId(medicoId);
        
        LocalDate hoy = LocalDate.now();
        
        for (Cita cita : todasCitas) {
            // Solo verificar citas futuras
            if (cita.getFecha().isAfter(hoy) || cita.getFecha().isEqual(hoy)) {
                // Verificar si es el mismo día de la semana
                if (cita.getFecha().getDayOfWeek().getValue() == diaSemana) {
                    LocalTime horaCita = cita.getHora();
                    // Verificar si la cita estaría fuera del nuevo rango
                    if (horaCita.isBefore(nuevoInicio) || horaCita.isAfter(nuevoFin) || horaCita.equals(nuevoFin)) {
                        return false; // Hay una cita que quedaría fuera
                    }
                }
            }
        }
        
        return true; // No hay conflictos
    }
    
    /**
     * Valida si se puede agendar una cita en un slot específico
     */
    public boolean validarDisponibilidadSlot(Long medicoId, LocalDate fecha, LocalTime hora) {
        // Verificar que la fecha no sea pasada
        if (fecha.isBefore(LocalDate.now())) {
            return false;
        }
        
        // Verificar que tenga horario configurado para ese día
        int diaSemana = fecha.getDayOfWeek().getValue();
        Optional<HorarioMedico> horarioOpt = obtenerHorarioDia(medicoId, diaSemana);
        
        if (horarioOpt.isEmpty()) {
            return false; // No trabaja ese día
        }
        
        HorarioMedico horario = horarioOpt.get();
        
        // Verificar que la hora esté dentro del rango
        if (hora.isBefore(horario.getHoraInicio()) || hora.isAfter(horario.getHoraFin()) || hora.equals(horario.getHoraFin())) {
            return false;
        }
        
        // Verificar que el slot no esté ocupado
        return !citaRepository.existsByMedicoIdAndFechaAndHora(medicoId, fecha, hora);
    }
    
    /**
     * Obtiene el nombre completo del paciente
     */
    private String obtenerNombrePaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
            .map(p -> p.getNombre() + " " + p.getApellido())
            .orElse("Desconocido");
    }
    
    /**
     * Desactiva un horario (soft delete)
     */
    public void desactivarHorario(Long medicoId, Integer diaSemana) {
        Optional<HorarioMedico> horarioOpt = horarioRepository.findByMedicoIdAndDiaSemana(medicoId, diaSemana);
        if (horarioOpt.isPresent()) {
            HorarioMedico horario = horarioOpt.get();
            horario.setActivo(false);
            horarioRepository.save(horario);
        }
    }
}
