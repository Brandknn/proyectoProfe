package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;

@Controller
public class HomeController {
    @Autowired
    private MedicoRepository medicoRepository;

    // holaaaaaaaa
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String usuario() {
        return "login";
    }

    // ELIMINADO: @GetMapping("/dictamen") - Ahora está en DictamenController

    @GetMapping("/horario")
    public String horario() {
        return "horario";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model,
            jakarta.servlet.http.HttpSession session) {
        Optional<Medico> medicoOpt = medicoRepository.findByEmailAndPassword(email, password);
        if (medicoOpt.isPresent()) {
            Medico medico = medicoOpt.get();
            session.setAttribute("medicoId", medico.getId());
            session.setAttribute("medicoEmail", medico.getEmail());
            return "redirect:/gestionCitas";
        } else {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }
    }
}