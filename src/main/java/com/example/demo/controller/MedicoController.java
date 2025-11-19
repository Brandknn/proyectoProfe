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
    public String agregarMedico(@ModelAttribute Medico medico, RedirectAttributes redirectAttributes) {
        medico.setPerfilCompleto(true);
        medicoRepository.save(medico);
        redirectAttributes.addFlashAttribute("successMessage", "¡Cuenta creada exitosamente! Ya puedes iniciar sesión");
        return "redirect:/login";
    }
}
