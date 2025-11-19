package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilMedicoController {

    private final MedicoRepository medicoRepository;

    public PerfilMedicoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @GetMapping("/PerfilMedicoAjustes")
    public String mostrarPerfil(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medico", medicoOpt.get());
        } else {
            return "redirect:/login";
        }

        return "PerfilMedicoAjustes";
    }

    @PostMapping("/actualizarPerfil")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam String telefono,
                                   @RequestParam String cedula,
                                   @RequestParam(required = false) String passwordActual,
                                   @RequestParam(required = false) String passwordNueva,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isEmpty()) {
            return "redirect:/login";
        }

        Medico medico = medicoOpt.get();

        medico.setNombre(nombre);
        medico.setTelefono(telefono);
        medico.setCedula(cedula);

        if (passwordActual != null && !passwordActual.isEmpty() && 
            passwordNueva != null && !passwordNueva.isEmpty()) {
            
            if (medico.getPassword() != null && !medico.getPassword().isEmpty()) {
                if (!medico.getPassword().equals(passwordActual)) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                    return "redirect:/PerfilMedicoAjustes";
                }
            }
            
            medico.setPassword(passwordNueva);
            redirectAttributes.addFlashAttribute("success", "Perfil y contraseña actualizados exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
        }

        medicoRepository.save(medico);
        
        return "redirect:/PerfilMedicoAjustes";
    }
}
