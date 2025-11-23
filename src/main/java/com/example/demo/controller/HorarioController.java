package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.HorarioMedico;
import com.example.demo.model.SlotDisponibilidad;
import com.example.demo.service.HorarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HorarioController {

    private final HorarioService horarioService;
    private final com.example.demo.repository.MedicoRepository medicoRepository;

    public HorarioController(HorarioService horarioService,
            com.example.demo.repository.MedicoRepository medicoRepository) {
        this.horarioService = horarioService;
        this.medicoRepository = medicoRepository;
    }

    /**
     * Muestra la página de configuración de horarios
     */
    @GetMapping("/horario")
    public String mostrarHorario(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        // Obtener médico logueado para mostrar en navegación
        java.util.Optional<com.example.demo.model.Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medicoLogueado", medicoOpt.get());
        }

        // Obtener horarios existentes del médico
        List<HorarioMedico> horarios = horarioService.obtenerHorariosMedico(medicoId);

        // Crear mapa con horarios por día (1-7)
        Map<Integer, HorarioMedico> horariosPorDia = new HashMap<>();
        for (HorarioMedico horario : horarios) {
            if (horario.getActivo()) {
                horariosPorDia.put(horario.getDiaSemana(), horario);
            }
        }

        model.addAttribute("horariosPorDia", horariosPorDia);
        return "horario";
    }

    /**
     * Guarda la configuración de horario
     */
    @PostMapping("/guardarHorario")
    public String guardarHorario(
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            // Procesar horarios para cada día (1-7: Lunes-Domingo)
            for (int dia = 1; dia <= 7; dia++) {
                String keyActivo = "dia" + dia + "_activo";
                String keyInicio = "dia" + dia + "_inicio";
                String keyFin = "dia" + dia + "_fin";

                boolean activo = params.containsKey(keyActivo) && "on".equals(params.get(keyActivo));

                if (activo) {
                    String horaInicio = params.get(keyInicio);
                    String horaFin = params.get(keyFin);

                    if (horaInicio != null && !horaInicio.isEmpty() &&
                            horaFin != null && !horaFin.isEmpty()) {

                        LocalTime inicio = LocalTime.parse(horaInicio);
                        LocalTime fin = LocalTime.parse(horaFin);

                        // Validar que inicio sea antes que fin
                        if (inicio.isAfter(fin) || inicio.equals(fin)) {
                            redirectAttributes.addFlashAttribute("error",
                                    "La hora de inicio debe ser anterior a la hora de fin para el día "
                                            + obtenerNombreDia(dia));
                            return "redirect:/horario";
                        }

                        // Validar que no afecte citas existentes
                        if (!horarioService.validarModificacionHorario(medicoId, dia, inicio, fin)) {
                            redirectAttributes.addFlashAttribute("error",
                                    "No se puede modificar el horario del " + obtenerNombreDia(dia) +
                                            " porque hay citas agendadas fuera del nuevo rango de horas.");
                            return "redirect:/horario";
                        }

                        horarioService.guardarHorario(medicoId, dia, inicio, fin);
                    }
                } else {
                    // Desactivar el horario para este día
                    horarioService.desactivarHorario(medicoId, dia);
                }
            }

            redirectAttributes.addFlashAttribute("mensaje", "Horario configurado exitosamente");
            return "redirect:/horario";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el horario: " + e.getMessage());
            return "redirect:/horario";
        }
    }

    /**
     * API para obtener slots disponibles de una fecha
     */
    @GetMapping("/api/slots-disponibles")
    public String obtenerSlotsDisponibles(
            @RequestParam String fecha,
            HttpSession session,
            Model model) {

        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "error";
        }

        try {
            LocalDate fechaConsulta = LocalDate.parse(fecha);
            List<SlotDisponibilidad> slots = horarioService.calcularSlotsDisponibles(medicoId, fechaConsulta);

            model.addAttribute("slots", slots);
            model.addAttribute("fecha", fechaConsulta);

            // Retornar fragment HTML para actualizar el calendario
            return "fragments/slots :: slotsLista";

        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener slots: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Utilidad para obtener nombre del día en español
     */
    private String obtenerNombreDia(int dia) {
        String[] dias = { "", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
        return dias[dia];
    }
}
