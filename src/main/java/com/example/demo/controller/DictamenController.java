package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Cita;
import com.example.demo.model.Dictamen;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.DictamenRepository;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DictamenController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DictamenRepository dictamenRepository;

    // Listar todos los dictámenes
    @GetMapping("/dictamen")
    public String listarDictamenes(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        System.out.println("GET /dictamen - Listando dictámenes del médico ID: " + medicoId);
        
        try {
            List<Cita> citas = citaRepository.findByMedicoIdOrderByFechaAscHoraAsc(medicoId);
            
            Map<Cita, Dictamen> citasConDictamen = new HashMap<>();
            
            for (Cita cita : citas) {
                Dictamen dictamen = dictamenRepository.findByCitaId(cita.getId()).orElse(null);
                citasConDictamen.put(cita, dictamen);
            }
            
            model.addAttribute("citasConDictamen", citasConDictamen);
            System.out.println("Citas encontradas: " + citas.size());
            
            return "listaDictamenes";
            
        } catch (Exception e) {
            System.err.println("Error al listar dictámenes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los dictámenes");
            return "listaDictamenes";
        }
    }

    // Mostrar formulario de dictamen
    @GetMapping("/cita/{id}/dictamen")
    public String mostrarDictamen(@PathVariable Long id, Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        System.out.println("========================================");
        System.out.println("GET /cita/" + id + "/dictamen");
        
        try {
            Cita cita = citaRepository.findById(id).orElse(null);
            
            if (cita == null) {
                System.err.println("ERROR: Cita no encontrada con ID: " + id);
                model.addAttribute("error", "La cita con ID " + id + " no existe");
                return "dictamen";
            }
            
            if (!cita.getMedicoId().equals(medicoId)) {
                System.err.println("ERROR: La cita no pertenece al médico actual");
                return "redirect:/dictamen";
            }
            
            System.out.println("Cita encontrada: ID=" + cita.getId() + ", Motivo=" + cita.getMotivo());

            Dictamen dictamen = dictamenRepository.findByCitaId(id)
                .orElseGet(() -> {
                    System.out.println("No existe dictamen, creando uno nuevo");
                    return new Dictamen("", id);
                });

            model.addAttribute("cita", cita);
            model.addAttribute("dictamen", dictamen);

            System.out.println("========================================");
            
            return "dictamen";
            
        } catch (Exception e) {
            System.err.println("ERROR en mostrarDictamen:");
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dictamen");
            return "dictamen";
        }
    }

    // Guardar dictamen
    @PostMapping("/cita/{id}/dictamen")
    public String guardarDictamen(
            @PathVariable Long id,
            @RequestParam String contenido,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        System.out.println("POST /cita/" + id + "/dictamen");
        
        try {
            Cita cita = citaRepository.findById(id).orElse(null);
            
            if (cita == null) {
                redirectAttributes.addFlashAttribute("error", "La cita no existe");
                return "redirect:/dictamen";
            }
            
            if (!cita.getMedicoId().equals(medicoId)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para modificar este dictamen");
                return "redirect:/dictamen";
            }

            if (contenido == null || contenido.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El contenido no puede estar vacío");
                return "redirect:/cita/" + id + "/dictamen";
            }

            Dictamen dictamen = dictamenRepository.findByCitaId(id)
                .orElseGet(() -> new Dictamen());

            dictamen.setContenido(contenido.trim());
            dictamen.setCitaId(id);

            dictamenRepository.save(dictamen);

            redirectAttributes.addFlashAttribute("mensaje", "Dictamen guardado correctamente");

            return "redirect:/cita/" + id + "/dictamen";
            
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al guardar el dictamen");
            return "redirect:/cita/" + id + "/dictamen";
        }
    }

    // NUEVO: Eliminar dictamen
    @PostMapping("/dictamen/eliminar/{id}")
    public String eliminarDictamen(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        System.out.println("POST /dictamen/eliminar/" + id);
        
        try {
            Dictamen dictamen = dictamenRepository.findById(id).orElse(null);
            
            if (dictamen == null) {
                redirectAttributes.addFlashAttribute("error", "Dictamen no encontrado");
                return "redirect:/dictamen";
            }
            
            // Verificar que la cita pertenezca al médico
            Cita cita = citaRepository.findById(dictamen.getCitaId()).orElse(null);
            if (cita == null || !cita.getMedicoId().equals(medicoId)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para eliminar este dictamen");
                return "redirect:/dictamen";
            }
            
            dictamenRepository.deleteById(id);
            System.out.println("Dictamen eliminado con ID: " + id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Dictamen eliminado correctamente");
            return "redirect:/dictamen";
            
        } catch (Exception e) {
            System.err.println("Error al eliminar dictamen: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el dictamen");
            return "redirect:/dictamen";
        }
    }
}