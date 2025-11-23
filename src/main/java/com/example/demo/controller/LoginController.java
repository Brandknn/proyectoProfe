package com.example.demo.controller;

import java.util.regex.Pattern;

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

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$");
    private static final Pattern DOCUMENT_PATTERN = Pattern.compile(
            "^[A-Za-z0-9]{5,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s.,-]{2,100}$");

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
            System.err.println(
                    "[OAuth2] Email nulo o vacío en atributos. Atributos recibidos: " + principal.getAttributes());
            return "redirect:/login?error=no-email";
        }

        Medico existente = medicoRepository.findByEmail(email).orElse(null);
        if (existente != null) {
            // Si el usuario ya existe en la BD
            if (Boolean.TRUE.equals(existente.getPerfilCompleto())) {
                // Ya tiene perfil completo, establecer sesión y redirigir
                System.out.println(
                        "[OAuth2] Usuario con email " + email + " ya tiene perfil completo. Redirigiendo a paciente");
                session.setAttribute("medicoId", existente.getId());
                session.setAttribute("medicoEmail", existente.getEmail());
                session.setAttribute("medicoNombre", existente.getNombre());
                return "redirect:/paciente";
            } else {
                // Existe pero no tiene perfil completo, debe completar registro
                System.out.println(
                        "[OAuth2] Usuario con email " + email
                                + " existe pero sin perfil completo. Redirigiendo a completar-registro-google");
                session.setAttribute("oauthEmail", email);
                session.setAttribute("oauthName", name != null ? name : "");
                session.setAttribute("oauthGoogleId", googleId);
                return "redirect:/completar-registro-google";
            }
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
        model.addAttribute("apellido", "");
        model.addAttribute("telefono", "");
        model.addAttribute("documento", "");
        return "completar-registro";
    }

    @PostMapping("/completar-registro-google")
    public String completarRegistro(@RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false, defaultValue = "") String apellido,
            @RequestParam(required = false, defaultValue = "") String telefono,
            @RequestParam(required = false, defaultValue = "") String documento,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("oauthEmail");
        if (email == null) {
            return "redirect:/login";
        }

        // Validación 1: Campos vacíos
        if (nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() ||
                telefono == null || telefono.trim().isEmpty() ||
                documento == null || documento.trim().isEmpty()) {
            model.addAttribute("error", "Todos los campos son requeridos");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Limpieza de datos
        nombre = sanitizeInput(nombre.trim());
        apellido = sanitizeInput(apellido.trim());
        telefono = sanitizeInput(telefono.trim());
        documento = sanitizeInput(documento.trim());
        email = sanitizeInput(email.trim());

        // Validación 2: Formato de email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            model.addAttribute("error", "El email proporcionado por Google no es válido");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 3: Formato de nombre
        if (!NAME_PATTERN.matcher(nombre).matches()) {
            model.addAttribute("error", "El nombre solo puede contener letras, espacios y puntos (2-100 caracteres)");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 4: Longitud de nombre
        if (nombre.length() < 2 || nombre.length() > 100) {
            model.addAttribute("error", "El nombre debe tener entre 2 y 100 caracteres");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 5: Formato de apellido
        if (!NAME_PATTERN.matcher(apellido).matches()) {
            model.addAttribute("error", "El apellido solo puede contener letras, espacios y puntos (2-100 caracteres)");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 6: Longitud de apellido
        if (apellido.length() < 2 || apellido.length() > 100) {
            model.addAttribute("error", "El apellido debe tener entre 2 y 100 caracteres");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 7: Formato de teléfono
        if (!PHONE_PATTERN.matcher(telefono).matches()) {
            model.addAttribute("error", "El formato del teléfono no es válido. Ej: +34912345678 o 912345678");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 8: Longitud de teléfono
        String telefonoDigits = telefono.replaceAll("[^0-9]", "");
        if (telefonoDigits.length() < 7 || telefonoDigits.length() > 15) {
            model.addAttribute("error", "El teléfono debe contener entre 7 y 15 dígitos");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 9: Formato de documento
        if (!DOCUMENT_PATTERN.matcher(documento).matches()) {
            model.addAttribute("error", "El documento debe contener solo letras y números (5-20 caracteres)");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 10: Longitud de documento
        if (documento.length() < 5 || documento.length() > 20) {
            model.addAttribute("error", "El documento debe tener entre 5 y 20 caracteres");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", documento);
            return "completar-registro";
        }

        // Validación 11: Verificar si el documento ya existe
        Medico medicoConDocumento = medicoRepository.findByCedula(documento).orElse(null);
        if (medicoConDocumento != null && !medicoConDocumento.getEmail().equals(email)) {
            model.addAttribute("error", "El número de documento ya está registrado en el sistema");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("documento", "");
            return "completar-registro";
        }

        // Validación 12: Verificar si el teléfono ya existe
        Medico medicoConTelefono = medicoRepository.findByTelefono(telefono).orElse(null);
        if (medicoConTelefono != null && !medicoConTelefono.getEmail().equals(email)) {
            model.addAttribute("error", "El número de teléfono ya está registrado en el sistema");
            model.addAttribute("email", email);
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", "");
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
        nuevo.setApellido(apellido);
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

    @GetMapping("/cancelar-oauth")
    public String cancelarOAuth(HttpSession session) {
        // Limpiar sesión OAuth
        session.removeAttribute("oauthEmail");
        session.removeAttribute("oauthName");
        session.removeAttribute("oauthGoogleId");
        System.out.println("[OAuth2] Sesión OAuth cancelada y limpiada");
        return "redirect:/login";
    }

    /**
     * Método para sanitizar input y prevenir inyección SQL/XSS
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remover caracteres peligrosos para SQL y XSS
        return input.replaceAll("[<>\"';()]", "")
                .replaceAll("--", "")
                .replaceAll("(?i)script", "")
                .replaceAll("(?i)alert", "")
                .replaceAll("(?i)onclick", "")
                .replaceAll("(?i)onerror", "")
                .replaceAll("(?i)onload", "")
                .replaceAll("(?i)javascript:", "");
    }
}
