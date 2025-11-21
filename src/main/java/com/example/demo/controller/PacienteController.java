package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final EmailService emailService;

    public PacienteController(PacienteRepository pacienteRepository, CitaRepository citaRepository,
            EmailService emailService) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
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
    public String agregarPaciente(@ModelAttribute Paciente paciente, Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            // Validar que el nombre no contenga números
            if (paciente.getNombre() != null && paciente.getNombre().matches(".*\\d.*")) {
                model.addAttribute("error", "El nombre no puede contener números.");
                model.addAttribute("paciente", new Paciente());
                List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                model.addAttribute("pacientes", pacientes);
                return "paciente";
            }

            // Validar que el apellido no contenga números
            if (paciente.getApellido() != null && paciente.getApellido().matches(".*\\d.*")) {
                model.addAttribute("error", "El apellido no puede contener números.");
                model.addAttribute("paciente", new Paciente());
                List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                model.addAttribute("pacientes", pacientes);
                return "paciente";
            }

            // Validar que el documento solo contenga números
            if (paciente.getDocumento() != null && !paciente.getDocumento().matches("[0-9]+")) {
                model.addAttribute("error", "El documento solo puede contener números.");
                model.addAttribute("paciente", new Paciente());
                List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                model.addAttribute("pacientes", pacientes);
                return "paciente";
            }

            // Validar que el teléfono sea válido (solo números y positivo)
            if (paciente.getTelefono() <= 0 && !String.valueOf(paciente.getTelefono()).matches("[0-9]+")) {
                model.addAttribute("error", "El teléfono debe ser un número positivo.");
                model.addAttribute("paciente", new Paciente());
                List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                model.addAttribute("pacientes", pacientes);
                return "paciente";
            }

            // Validar que el documento no esté duplicado
            Paciente pacienteExistente = pacienteRepository.findByDocumento(paciente.getDocumento());
            if (pacienteExistente != null) {
                model.addAttribute("error", "El documento ya está registrado en el sistema.");
                model.addAttribute("paciente", new Paciente());
                List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                model.addAttribute("pacientes", pacientes);
                return "paciente";
            }

            // Validar que el teléfono no esté duplicado
            Paciente pacienteTelefonoDuplicado = pacienteRepository.findByTelefono(paciente.getTelefono());
            if (pacienteTelefonoDuplicado != null) {
                model.addAttribute("error", "El teléfono ya está registrado en el sistema.");
                model.addAttribute("paciente", new Paciente());
                List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                model.addAttribute("pacientes", pacientes);
                return "paciente";
            }

            // Validar que el correo no esté duplicado (si se proporciona)
            if (paciente.getCorreo() != null && !paciente.getCorreo().isEmpty()) {
                Paciente pacienteCorreoDuplicado = pacienteRepository.findByCorreo(paciente.getCorreo());
                if (pacienteCorreoDuplicado != null) {
                    model.addAttribute("error", "El correo ya está registrado en el sistema.");
                    model.addAttribute("paciente", new Paciente());
                    List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
                    model.addAttribute("pacientes", pacientes);
                    return "paciente";
                }
            }

            paciente.setMedicoId(medicoId);
            pacienteRepository.save(paciente);
            if (paciente.getCorreo() != null && !paciente.getCorreo().isEmpty()) {
                String asunto = "Bienvenido a la clínica";
                String mensaje = "Hola " + paciente.getNombre() + " " + paciente.getApellido()
                        + ", tu registro fue exitoso.";
                emailService.enviarCorreo(paciente.getCorreo(), asunto, mensaje);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al agregar el paciente: " + e.getMessage());
            model.addAttribute("paciente", new Paciente());
            List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
            model.addAttribute("pacientes", pacientes);
            return "paciente";
        }
        return "redirect:/paciente";
    }

    @PostMapping("/eliminarPaciente")
    public String eliminarPaciente(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        System.out.println("[DELETE] medicoId from session: " + medicoId);

        if (medicoId == null || id == null) {
            System.out.println("[DELETE] medicoId or id is null, redirecting to login");
            return "redirect:/login";
        }

        Optional<Paciente> pacienteOpt = id != null ? pacienteRepository.findById(id) : Optional.empty();
        System.out.println("[DELETE] Found paciente: " + pacienteOpt.isPresent());

        if (pacienteOpt.isPresent() && pacienteOpt.get().getMedicoId().equals(medicoId)) {
            System.out.println("[DELETE] Deleting paciente with id: " + id);
            // Primero eliminar todas las citas del paciente
            citaRepository.deleteByPacienteId(id);
            System.out.println("[DELETE] Deleted all citas for paciente: " + id);
            // Luego eliminar el paciente
            pacienteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Paciente eliminado exitosamente.");
            System.out.println("[DELETE] Successfully deleted, redirecting to /paciente");
        } else {
            System.out.println("[DELETE] Paciente not found or medicoId mismatch");
        }

        return "redirect:/paciente";
    }

    @PostMapping("/actualizarPaciente")
    public String actualizarPaciente(@ModelAttribute Paciente paciente, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null || paciente == null || paciente.getId() == null) {
            return "redirect:/login";
        }

        Optional<Paciente> pacienteExistente = paciente.getId() != null ? pacienteRepository.findById(paciente.getId())
                : Optional.empty();
        if (pacienteExistente.isPresent() && pacienteExistente.get().getMedicoId().equals(medicoId)) {
            paciente.setMedicoId(medicoId);
            pacienteRepository.save(paciente);
        }

        return "redirect:/paciente";
    }

    @GetMapping("/api/verificar-documento")
    @ResponseBody
    public String verificarDocumento(@RequestParam String documento, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "error";
        }

        Paciente paciente = pacienteRepository.findByDocumento(documento);
        if (paciente != null && paciente.getMedicoId().equals(medicoId)) {
            return "duplicado";
        }
        return "disponible";
    }

    @GetMapping("/api/verificar-correo")
    @ResponseBody
    public String verificarCorreo(@RequestParam String correo, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null || correo.isEmpty()) {
            return "disponible";
        }

        Paciente paciente = pacienteRepository.findByCorreo(correo);
        if (paciente != null && paciente.getMedicoId().equals(medicoId)) {
            return "duplicado";
        }
        return "disponible";
    }

    @GetMapping("/api/verificar-telefono")
    @ResponseBody
    public String verificarTelefono(@RequestParam Long telefono, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null || telefono == null) {
            return "disponible";
        }

        Paciente paciente = pacienteRepository.findByTelefono(telefono);
        if (paciente != null && paciente.getMedicoId().equals(medicoId)) {
            return "duplicado";
        }
        return "disponible";
    }
}
