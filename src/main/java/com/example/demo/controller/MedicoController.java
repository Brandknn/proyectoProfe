package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;

@Controller
public class MedicoController {
    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping("/CrearCuentaMedico")
    public String mostrarFormulario(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "CrearCuentaMedico";
    }

    @PostMapping("/agregarMedico")
    public String agregarMedico(@ModelAttribute Medico medico, Model model, RedirectAttributes redirectAttributes) {
        // Validar que el nombre no contenga números
        if (medico.getNombre() != null && medico.getNombre().matches(".*\\d.*")) {
            model.addAttribute("error", "El nombre no puede contener números.");
            model.addAttribute("medico", new Medico());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "CrearCuentaMedico";
        }
        
        // Validar que la cédula solo contenga números
        if (medico.getCedula() != null && !medico.getCedula().matches("[0-9]+")) {
            model.addAttribute("error", "La cédula solo puede contener números.");
            model.addAttribute("medico", new Medico());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "CrearCuentaMedico";
        }
        
        // Validar que el teléfono solo contenga números
        if (medico.getTelefono() != null && !medico.getTelefono().matches("[0-9]+")) {
            model.addAttribute("error", "El teléfono solo puede contener números.");
            model.addAttribute("medico", new Medico());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "CrearCuentaMedico";
        }
        
        // Validar que el email no esté duplicado
        if (medicoRepository.findByEmail(medico.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado en el sistema.");
            model.addAttribute("medico", new Medico());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "CrearCuentaMedico";
        }
        
        // Validar que la cédula no esté duplicada
        if (medicoRepository.findByCedula(medico.getCedula()).isPresent()) {
            model.addAttribute("error", "La cédula ya está registrada en el sistema.");
            model.addAttribute("medico", new Medico());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "CrearCuentaMedico";
        }
        
        medico.setPerfilCompleto(true);
        medicoRepository.save(medico);
        redirectAttributes.addFlashAttribute("successMessage", "¡Cuenta creada exitosamente! Ya puedes iniciar sesión");
        return "redirect:/login";
    }
}
