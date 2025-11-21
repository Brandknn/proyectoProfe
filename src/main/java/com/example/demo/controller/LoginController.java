package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
public class LoginController {
    
    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping("/oauth2/home")
    public String oauthHome(@AuthenticationPrincipal OAuth2User principal, Model model, HttpSession session) {
        if (principal == null) {
            System.err.println("[OAuth2] Principal nulo: no se recibió usuario desde el proveedor");
            return "redirect:/login?error=principal-null";
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String googleId = principal.getName();
        if (email == null || email.isBlank()) {
            System.err.println("[OAuth2] Email nulo o vacío en atributos. Atributos recibidos: " + principal.getAttributes());
            return "redirect:/login?error=no-email";
        }

        Medico existente = medicoRepository.findByEmail(email).orElse(null);
        if (existente != null && Boolean.TRUE.equals(existente.getPerfilCompleto())) {
            System.out.println("[OAuth2] Usuario con email " + email + " ya tiene perfil completo. Redirigiendo a paciente");
            session.setAttribute("medicoId", existente.getId());
            session.setAttribute("medicoEmail", existente.getEmail());
            return "redirect:/paciente"; // ya estaba registrado totalmente por otra vía
        }

        // Solo guardar datos en sesión; la creación ocurrirá tras enviar el formulario
        session.setAttribute("oauthEmail", email);
        session.setAttribute("oauthName", name != null ? name : "");
        session.setAttribute("oauthGoogleId", googleId);
        System.out.println("[OAuth2] Redirigiendo a completar-registro-google para email: " + email);
        return "redirect:/completar-registro-google";
    }
    
    @GetMapping("/completar-registro-google")
    public String mostrarFormularioRegistro(Model model, HttpSession session) {
        String email = (String) session.getAttribute("oauthEmail");
        String nombre = (String) session.getAttribute("oauthName");
        
        if (email == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("email", email);
        model.addAttribute("nombre", nombre != null ? nombre : "");
        model.addAttribute("telefono", "");
        model.addAttribute("documento", "");
        return "completar-registro";
    }
    
    @PostMapping("/completar-registro-google")
    public String completarRegistro(@RequestParam String nombre,
                                    @RequestParam String telefono,
                                    @RequestParam String documento,
                                    Model model,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        
        String email = (String) session.getAttribute("oauthEmail");
        if (email == null) {
            return "redirect:/login";
        }
        
        // Validate inputs
        if (nombre == null || nombre.trim().isEmpty() ||
            telefono == null || telefono.trim().isEmpty() ||
            documento == null || documento.trim().isEmpty()) {
            model.addAttribute("error", "Todos los campos son requeridos");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }
        
        // Si ya existe y completo, redirigir
        Medico existente = medicoRepository.findByEmail(email).orElse(null);
        if (existente != null && Boolean.TRUE.equals(existente.getPerfilCompleto())) {
            session.setAttribute("medicoId", existente.getId());
            session.setAttribute("medicoEmail", existente.getEmail());
            return "redirect:/paciente";
        }

        // Crear nuevo solamente ahora (registro alternativo)
        Medico nuevo = (existente != null) ? existente : new Medico();
        nuevo.setEmail(email);
        nuevo.setNombre(nombre);
        nuevo.setTelefono(telefono);
        nuevo.setCedula(documento);
        nuevo.setPerfilCompleto(true);
        nuevo.setPassword("");
        String googleId = (String) session.getAttribute("oauthGoogleId");
        if (googleId != null && (nuevo.getGoogleId() == null || nuevo.getGoogleId().isBlank())) {
            nuevo.setGoogleId(googleId);
        }
        medicoRepository.save(nuevo);
        
        // Store medico ID in session
        session.setAttribute("medicoId", nuevo.getId());
        session.setAttribute("medicoEmail", nuevo.getEmail());
        
        // Clean up session
        session.removeAttribute("oauthEmail");
        session.removeAttribute("oauthName");
        
        redirectAttributes.addFlashAttribute("success", "¡Registro completado exitosamente!");
        return "redirect:/paciente";
    }
}
