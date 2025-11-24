package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Cita;
import com.example.demo.model.Dictamen;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.DictamenRepository;
import com.example.demo.repository.PacienteRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DictamenController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DictamenRepository dictamenRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/dictamen")
    public String listarDictamenes(Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            List<Cita> citas = citaRepository.findByMedicoIdOrderByFechaAscHoraAsc(medicoId);

            // Mapas separados para nombres y apellidos
            Map<Long, String> pacienteNombres = new HashMap<>();
            Map<Long, String> pacienteApellidos = new HashMap<>();
            List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
            for (Paciente p : pacientes) {
                pacienteNombres.put(p.getId(), p.getNombre());
                pacienteApellidos.put(p.getId(), p.getApellido());
            }
            model.addAttribute("pacienteNombres", pacienteNombres);
            model.addAttribute("pacienteApellidos", pacienteApellidos);

            Map<Cita, Dictamen> citasConDictamen = new HashMap<>();
            for (Cita cita : citas) {
                Dictamen dictamen = dictamenRepository.findByCitaId(cita.getId()).orElse(null);
                citasConDictamen.put(cita, dictamen);
            }

            model.addAttribute("citasConDictamen", citasConDictamen);
            return "listaDictamenes";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los dictámenes");
            return "listaDictamenes";
        }
    }

    @GetMapping("/cita/{id}/dictamen")
    public String mostrarDictamen(@PathVariable Long id, Model model, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            Cita cita = citaRepository.findById(id).orElse(null);

            if (cita == null) {
                model.addAttribute("error", "La cita con ID " + id + " no existe");
                return "dictamen";
            }

            if (!cita.getMedicoId().equals(medicoId)) {
                return "redirect:/dictamen";
            }

            Paciente paciente = null;
            if (cita.getPacienteId() != null) {
                paciente = pacienteRepository.findById(cita.getPacienteId()).orElse(null);
            }

            Dictamen dictamen = dictamenRepository.findByCitaId(id)
                    .orElseGet(() -> new Dictamen("", id));

            model.addAttribute("cita", cita);
            model.addAttribute("paciente", paciente);
            model.addAttribute("dictamen", dictamen);

            return "dictamen";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dictamen");
            return "dictamen";
        }
    }

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

        try {
            Cita cita = citaRepository.findById(id).orElse(null);

            if (cita == null) {
                redirectAttributes.addFlashAttribute("error", "La cita no existe");
                return "redirect:/dictamen";
            }

            if (!cita.getMedicoId().equals(medicoId)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos");
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
            redirectAttributes.addFlashAttribute("error", "Error al guardar el dictamen");
            return "redirect:/cita/" + id + "/dictamen";
        }
    }

    @PostMapping("/dictamen/eliminar/{id}")
    public String eliminarDictamen(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");
        if (medicoId == null) {
            return "redirect:/login";
        }

        try {
            Dictamen dictamen = dictamenRepository.findById(id).orElse(null);

            if (dictamen == null) {
                redirectAttributes.addFlashAttribute("error", "Dictamen no encontrado");
                return "redirect:/dictamen";
            }

            Cita cita = citaRepository.findById(dictamen.getCitaId()).orElse(null);
            if (cita == null || !cita.getMedicoId().equals(medicoId)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos");
                return "redirect:/dictamen";
            }

            dictamenRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Dictamen eliminado correctamente");
            return "redirect:/dictamen";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el dictamen");
            return "redirect:/dictamen";
        }
    }
}