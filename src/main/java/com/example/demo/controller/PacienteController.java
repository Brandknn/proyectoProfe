package com.example.demo.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final EmailService emailService;

    public PacienteController(PacienteRepository pacienteRepository, EmailService emailService) {
        this.pacienteRepository = pacienteRepository;
        this.emailService = emailService;
    }

    @GetMapping("/paciente")
    public String mostrarPacientes(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("paciente", new Paciente());
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        model.addAttribute("pacientes", pacientes);
        return "paciente";
    }

    @PostMapping("/agregarPaciente")
    public String agregarPaciente(@ModelAttribute Paciente paciente, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        paciente.setMedicoId(medicoId);
        pacienteRepository.save(paciente);
        if (paciente.getCorreo() != null && !paciente.getCorreo().isEmpty()) {
            String asunto = "Bienvenido a la clínica";
            String mensaje = "Hola " + paciente.getNombre() + ", tu registro fue exitoso.";
            emailService.enviarCorreo(paciente.getCorreo(), asunto, mensaje);
        }
        return "redirect:/paciente";
    }

    @PostMapping("/eliminarPaciente")
    public String eliminarPaciente(@RequestParam Long id, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent() && pacienteOpt.get().getMedicoId().equals(medicoId)) {
            pacienteRepository.deleteById(id);
        }
        
        return "redirect:/paciente";
    }

    @PostMapping("/actualizarPaciente")
    public String actualizarPaciente(@ModelAttribute Paciente paciente, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        Optional<Paciente> pacienteExistente = pacienteRepository.findById(paciente.getId());
        if (pacienteExistente.isPresent() && pacienteExistente.get().getMedicoId().equals(medicoId)) {
            paciente.setMedicoId(medicoId);
            pacienteRepository.save(paciente);
        }
        
        return "redirect:/paciente";
    }
}
