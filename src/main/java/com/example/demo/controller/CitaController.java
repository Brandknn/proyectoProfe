package com.example.demo.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Cita;
import com.example.demo.model.CitaConPaciente;
import com.example.demo.model.EstadoCita;
import com.example.demo.model.Medico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.DictamenRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.HorarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CitaController {
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final DictamenRepository dictamenRepository; // NUEVO
    private final EmailService emailService;
    private final HorarioService horarioService;

    public CitaController(CitaRepository citaRepository, PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository, DictamenRepository dictamenRepository,
            EmailService emailService, HorarioService horarioService) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.dictamenRepository = dictamenRepository; // NUEVO
        this.emailService = emailService;
        this.horarioService = horarioService;
    }

    @GetMapping("/gestionCitas")
    public String mostrarGestionCitas(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        // Obtener médico logueado para mostrar en navegación
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medicoLogueado", medicoOpt.get());
        }

        model.addAttribute("cita", new Cita());
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        model.addAttribute("pacientes", pacientes);

        Map<Long, String> pacienteNombres = new HashMap<>();
        for (Paciente p : pacientes) {
            pacienteNombres.put(p.getId(), p.getNombre() + " " + p.getApellido());
        }

        List<Cita> citas = citaRepository.findByMedicoIdOrderByFechaAscHoraAsc(medicoId);
        List<CitaConPaciente> citasConPaciente = new ArrayList<>();
        for (Cita cita : citas) {
            String nombrePaciente = pacienteNombres.getOrDefault(cita.getPacienteId(), "Paciente no encontrado");
            citasConPaciente.add(new CitaConPaciente(cita, nombrePaciente));
        }
        model.addAttribute("citas", citasConPaciente);

        return "gestionCitas";
    }

    @PostMapping("/agregarCita")
    public String agregarCita(@ModelAttribute Cita cita, HttpSession session, RedirectAttributes redirectAttributes) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            if (cita.getPacienteId() == null) {
                redirectAttributes.addFlashAttribute("error", "Paciente no seleccionado");
                return "redirect:/gestionCitas";
            }
            if (cita.getFecha() == null || cita.getHora() == null) {
                redirectAttributes.addFlashAttribute("error", "Fecha y hora requeridas");
                return "redirect:/gestionCitas";
            }

            // Validar que no se programe una cita en el pasado (solo si es para HOY)
            java.time.LocalDate hoy = java.time.LocalDate.now();
            if (cita.getFecha().isEqual(hoy)) {
                java.time.LocalTime ahora = java.time.LocalTime.now();
                if (cita.getHora().isBefore(ahora)) {
                    redirectAttributes.addFlashAttribute("error",
                            "No puede programar una cita para una hora que ya pasó. Hora actual: " +
                                    ahora.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
                    return "redirect:/gestionCitas";
                }
            }

            // Validar disponibilidad del slot
            if (!horarioService.validarDisponibilidadSlot(medicoId, cita.getFecha(), cita.getHora())) {
                // Determinar razón específica del rechazo
                java.time.DayOfWeek diaSemana = cita.getFecha().getDayOfWeek();
                int diaNum = diaSemana.getValue();
                boolean tieneHorario = horarioService.obtenerHorarioDia(medicoId, diaNum).isPresent();

                if (!tieneHorario) {
                    String nombreDia = diaSemana.getDisplayName(
                            java.time.format.TextStyle.FULL,
                            java.util.Locale.forLanguageTag("es"));
                    redirectAttributes.addFlashAttribute("error",
                            "No tiene horario configurado para " + nombreDia
                                    + ". Por favor configure su horario primero en la sección Horarios.");
                } else {
                    redirectAttributes.addFlashAttribute("error",
                            "El horario seleccionado no está disponible. Puede estar ocupado o fuera de su rango de atención.");
                }
                return "redirect:/gestionCitas";
            }
            cita.setMedicoId(medicoId);
            citaRepository.save(cita);

            if (cita.getPacienteId() != null) {
                Optional<Paciente> pacienteOpt = pacienteRepository.findById(cita.getPacienteId());
                Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);

                if (pacienteOpt.isPresent() && medicoOpt.isPresent()) {
                    Paciente paciente = pacienteOpt.get();
                    Medico medico = medicoOpt.get();

                    if (paciente.getCorreo() != null && !paciente.getCorreo().isEmpty() && medico.getEmail() != null) {
                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");

                        String fechaFormateada = cita.getFecha().format(formatoFecha);
                        String horaFormateada = cita.getHora().format(formatoHora);

                        String asunto = "Confirmación de Cita Médica";
                        String mensaje = String.format(
                                "Estimado(a) %s,\n\n" +
                                        "Se le ha asignado una cita médica con los siguientes detalles:\n\n" +
                                        "Médico: %s\n" +
                                        "Fecha: %s\n" +
                                        "Hora: %s\n" +
                                        "Motivo: %s\n\n" +
                                        "Por favor, asegúrese de asistir puntualmente.\n\n" +
                                        "Saludos cordiales,\n" +
                                        "Consultorio Médico",
                                paciente.getNombre() + " " + paciente.getApellido(),
                                medico.getNombre(),
                                fechaFormateada,
                                horaFormateada,
                                cita.getMotivo());

                        emailService.enviarCorreo(medico.getEmail(), paciente.getCorreo(), asunto, mensaje);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("mensaje", "Cita creada exitosamente");
            return "redirect:/gestionCitas";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cita: " + e.getMessage());
            return "redirect:/gestionCitas";
        }
    }

    @PostMapping("/eliminarCita")
    public String eliminarCita(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null || id == null) {
            return "redirect:/login";
        }

        try {
            Optional<Cita> citaOpt = citaRepository.findById(id);
            if (citaOpt.isPresent()) {
                Long citaMedicoId = citaOpt.get().getMedicoId();
                if (citaMedicoId != null && citaMedicoId.equals(medicoId)) {
                    // PRIMERO eliminar el dictamen asociado (si existe)
                    dictamenRepository.deleteByCitaId(id);
                    // LUEGO eliminar la cita
                    citaRepository.deleteById(id);
                    redirectAttributes.addFlashAttribute("mensaje", "Cita eliminada exitosamente");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cita: " + e.getMessage());
        }

        return "redirect:/gestionCitas";
    }

    @PostMapping("/actualizarCita")
    public String actualizarCita(@ModelAttribute Cita cita, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null || cita == null || cita.getId() == null) {
            return "redirect:/login";
        }

        try {
            Optional<Cita> citaExistenteOpt = citaRepository.findById(cita.getId());
            if (citaExistenteOpt.isPresent() &&
                    citaExistenteOpt.get().getMedicoId() != null &&
                    citaExistenteOpt.get().getMedicoId().equals(medicoId)) {
                Cita citaExistente = citaExistenteOpt.get();

                // Validar que no se mueva a una fecha pasada
                java.time.LocalDate hoy = java.time.LocalDate.now();
                if (cita.getFecha().isBefore(hoy)) {
                    redirectAttributes.addFlashAttribute("error", "No se puede mover una cita a una fecha pasada.");
                    return "redirect:/gestionCitas";
                }

                // Si cambia fecha u hora, validar disponibilidad
                if (!cita.getFecha().equals(citaExistente.getFecha())
                        || !cita.getHora().equals(citaExistente.getHora())) {
                    if (!horarioService.validarDisponibilidadSlot(medicoId, cita.getFecha(), cita.getHora())) {
                        redirectAttributes.addFlashAttribute("error",
                                "El nuevo horario seleccionado no está disponible.");
                        return "redirect:/gestionCitas";
                    }
                }

                citaExistente.setPacienteId(cita.getPacienteId());
                citaExistente.setFecha(cita.getFecha());
                citaExistente.setHora(cita.getHora());
                citaExistente.setMotivo(cita.getMotivo());

                citaRepository.save(citaExistente);
                redirectAttributes.addFlashAttribute("mensaje", "Cita actualizada exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada o no autorizada.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cita: " + e.getMessage());
        }

        return "redirect:/gestionCitas";
    }

    @PostMapping("/cambiarEstadoCita")
    public String cambiarEstadoCita(@RequestParam Long citaId, @RequestParam String nuevoEstado, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            Optional<Cita> citaOpt = citaRepository.findById(citaId);
            if (citaOpt.isPresent() && citaOpt.get().getMedicoId().equals(medicoId)) {
                Cita cita = citaOpt.get();
                EstadoCita estadoEnum = EstadoCita.valueOf(nuevoEstado);

                // Validaciones de negocio
                java.time.LocalDate hoy = java.time.LocalDate.now();
                java.time.LocalTime ahora = java.time.LocalTime.now();
                boolean esFuturo = cita.getFecha().isAfter(hoy)
                        || (cita.getFecha().isEqual(hoy) && cita.getHora().isAfter(ahora));

                if (esFuturo && (estadoEnum == EstadoCita.COMPLETADA || estadoEnum == EstadoCita.NO_ASISTIO)) {
                    redirectAttributes.addFlashAttribute("error",
                            "No se puede marcar como Completada o No Asistió una cita futura.");
                    return "redirect:/gestionCitas";
                }

                if (!esFuturo && estadoEnum == EstadoCita.CANCELADA && cita.getEstado() != EstadoCita.PENDIENTE) {
                    // Permitir cancelar si estaba pendiente, pero advertir si ya pasó (aunque la
                    // lógica de negocio podría variar,
                    // generalmente cancelar algo pasado es raro a menos que sea para corregir un
                    // error)
                }

                cita.setEstado(estadoEnum);
                citaRepository.save(cita);
                redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado a: " + estadoEnum.getDescripcion());
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Estado inválido.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }

        return "redirect:/gestionCitas";
    }

    @GetMapping("/historialCitas")
    public String mostrarHistorialCitas(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        // Obtener médico logueado para mostrar en navegación
        java.util.Optional<com.example.demo.model.Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medicoLogueado", medicoOpt.get());
        }

        // Obtener todas las citas ordenadas
        List<Cita> todasLasCitas = citaRepository.findByMedicoIdOrderByFechaAscHoraAsc(medicoId);

        // Obtener pacientes para mapear nombres
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        Map<Long, String> pacienteNombres = new HashMap<>();
        for (Paciente p : pacientes) {
            pacienteNombres.put(p.getId(), p.getNombre() + " " + p.getApellido());
        }

        // Filtrar citas pasadas o finalizadas
        List<CitaConPaciente> historial = new ArrayList<>();
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalTime ahora = java.time.LocalTime.now();

        for (Cita cita : todasLasCitas) {
            boolean esPasado = cita.getFecha().isBefore(hoy)
                    || (cita.getFecha().isEqual(hoy) && cita.getHora().isBefore(ahora));
            boolean esFinalizado = cita.getEstado() == EstadoCita.COMPLETADA || cita.getEstado() == EstadoCita.CANCELADA
                    || cita.getEstado() == EstadoCita.NO_ASISTIO;

            if (esPasado || esFinalizado) {
                String nombrePaciente = pacienteNombres.getOrDefault(cita.getPacienteId(), "Paciente no encontrado");
                historial.add(new CitaConPaciente(cita, nombrePaciente));
            }
        }

        model.addAttribute("citas", historial);
        return "historialCitas";
    }

    @GetMapping("/calendarioMedico")
    public String mostrarCalendarioMedico(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        // Obtener médico logueado para mostrar en navegación
        java.util.Optional<com.example.demo.model.Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medicoLogueado", medicoOpt.get());
        }

        // Obtener todas las citas para el calendario
        List<Cita> citas = citaRepository.findByMedicoId(medicoId);

        // Mapear nombres de pacientes
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        Map<Long, String> pacienteNombres = new HashMap<>();
        for (Paciente p : pacientes) {
            pacienteNombres.put(p.getId(), p.getNombre() + " " + p.getApellido());
        }

        List<Map<String, Object>> eventos = new ArrayList<>();
        for (Cita cita : citas) {
            Map<String, Object> evento = new HashMap<>();
            evento.put("fecha", cita.getFecha().toString()); // YYYY-MM-DD
            evento.put("hora", cita.getHora().toString());
            evento.put("paciente", pacienteNombres.getOrDefault(cita.getPacienteId(), "Desconocido"));
            evento.put("estado", cita.getEstado().name());
            evento.put("motivo", cita.getMotivo());
            eventos.add(evento);
        }

        model.addAttribute("eventos", eventos);
        return "calendarioMedico";
    }

    @GetMapping("/limpiar-bd")
    public String limpiarBD() {
        dictamenRepository.deleteAll(); // Eliminar dictámenes primero
        citaRepository.deleteAll();
        pacienteRepository.deleteAll();
        medicoRepository.deleteAll();
        return "redirect:/";
    }
}
