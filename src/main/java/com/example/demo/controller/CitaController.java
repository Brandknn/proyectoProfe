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

import com.example.demo.model.Cita;
import com.example.demo.model.CitaConPaciente;
import com.example.demo.model.Medico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CitaController {
    // Controlador para la gestión de citas médicas
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final EmailService emailService;

    public CitaController(CitaRepository citaRepository, PacienteRepository pacienteRepository,
                         MedicoRepository medicoRepository, EmailService emailService) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.emailService = emailService;
    }

    @GetMapping("/gestionCitas")
    public String mostrarGestionCitas(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        model.addAttribute("cita", new Cita());
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        model.addAttribute("pacientes", pacientes);
        
        Map<Long, String> pacienteNombres = new HashMap<>();
        for (Paciente p : pacientes) {
            pacienteNombres.put(p.getId(), p.getNombre());
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
    public String agregarCita(@ModelAttribute Cita cita, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        cita.setMedicoId(medicoId);
        citaRepository.save(cita);
        
        
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(cita.getPacienteId());
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        
        if (pacienteOpt.isPresent() && medicoOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            Medico medico = medicoOpt.get();
            
            if (paciente.getCorreo() != null && !paciente.getCorreo().isEmpty()) {
               
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
                    paciente.getNombre(),
                    medico.getNombre(),
                    fechaFormateada,
                    horaFormateada,
                    cita.getMotivo()
                );
                
                
                emailService.enviarCorreo(medico.getEmail(), paciente.getCorreo(), asunto, mensaje);
            }
        }
        
        return "redirect:/gestionCitas";
    }

    @PostMapping("/eliminarCita")
    public String eliminarCita(@RequestParam Long id, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        Optional<Cita> citaOpt = citaRepository.findById(id);
        if (citaOpt.isPresent() && citaOpt.get().getMedicoId().equals(medicoId)) {
            citaRepository.deleteById(id);
        }

        return "redirect:/gestionCitas";
    }

    @PostMapping("/actualizarCita")
    public String actualizarCita(@ModelAttribute Cita cita, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        Optional<Cita> citaExistente = citaRepository.findById(cita.getId());
        if (citaExistente.isPresent() && citaExistente.get().getMedicoId().equals(medicoId)) {
            cita.setMedicoId(medicoId);
            citaRepository.save(cita);
        }

        return "redirect:/gestionCitas";
    }

    @GetMapping("/limpiar-bd")
    public String limpiarBD() {
        citaRepository.deleteAll();
        pacienteRepository.deleteAll();
        medicoRepository.deleteAll();
        return "redirect:/";
    }


    //yoyoyooyyoo
}
