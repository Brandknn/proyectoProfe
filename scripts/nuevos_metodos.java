@PostMapping("/actualizarCita")
public String actualizarCita(@ModelAttribute Cita cita, HttpSession session, RedirectAttributes redirectAttributes) {
    Long medicoId = (Long) session.getAttribute("medicoId");
    if (medicoId == null || cita == null || cita.getId() == null) {
        return "redirect:/login";
    }

    try {
        // Verificar que la cita existe y pertenece al médico
        Optional<Cita> citaExistente = citaRepository.findById(cita.getId());
        if (!citaExistente.isPresent() || !citaExistente.get().getMedicoId().equals(medicoId)) {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/gestionCitas";
        }

        // Validaciones básicas
        if (cita.getPacienteId() == null) {
            redirectAttributes.addFlashAttribute("error", "Paciente no seleccionado");
            return "redirect:/gestionCitas";
        }
        if (cita.getFecha() == null || cita.getHora() == null) {
            redirectAttributes.addFlashAttribute("error", "Fecha y hora requeridas");
            return "redirect:/gestionCitas";
        }

        // Validar que no se programe en el pasado (solo si es para HOY)
        java.time.LocalDate hoy = java.time.LocalDate.now();
        if (cita.getFecha().isEqual(hoy)) {
            java.time.LocalTime ahora = java.time.LocalTime.now();
            if (cita.getHora().isBefore(ahora)) {
                redirectAttributes.addFlashAttribute("error",
                        "No puede programar una cita para una hora que ya pasó. Hora actual: " +
                                ahora.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
                return "redirect:/gestionCitas";
            }
        }

        // Si cambiaron fecha u hora, validar disponibilidad del slot
        Cita citaAnterior = citaExistente.get();
        boolean cambioFechaHora = !cita.getFecha().equals(citaAnterior.getFecha()) ||
                !cita.getHora().equals(citaAnterior.getHora());

        if (cambioFechaHora) {
            if (!horarioService.validarDisponibilidadSlot(medicoId, cita.getFecha(), cita.getHora())) {
                java.time.DayOfWeek diaSemana = cita.getFecha().getDayOfWeek();
                int diaNum = diaSemana.getValue();
                boolean tieneHorario = horarioService.obtenerHorarioDia(medicoId, diaNum).isPresent();

                if (!tieneHorario) {
                    String nombreDia = diaSemana.getDisplayName(
                            java.time.format.TextStyle.FULL,
                            java.util.Locale.forLanguageTag("es"));
                    redirectAttributes.addFlashAttribute("error",
                            "No tiene horario configurado para " + nombreDia);
                } else {
                    redirectAttributes.addFlashAttribute("error",
                            "El horario seleccionado no está disponible");
                }
                return "redirect:/gestionCitas";
            }
        }

        // Guardar cambios
        cita.setMedicoId(medicoId);
        citaRepository.save(cita);

        redirectAttributes.addFlashAttribute("mensaje", "Cita actualizada exitosamente");
        return "redirect:/gestionCitas";

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar la cita: " + e.getMessage());
        return "redirect:/gestionCitas";
    }
}

// Helper method: Determinar estados permitidos según fecha
private List<com.example.demo.model.EstadoCita> getEstadosPermitidos(java.time.LocalDate fechaCita) {
    java.time.LocalDate hoy = java.time.LocalDate.now();
    List<com.example.demo.model.EstadoCita> estados = new ArrayList<>();

    if (fechaCita.isAfter(hoy)) {
        // Futuro: solo PENDIENTE y CANCELADO
        estados.add(com.example.demo.model.EstadoCita.PENDIENTE);
        estados.add(com.example.demo.model.EstadoCita.CANCELADO);
    } else if (fechaCita.isEqual(hoy)) {
        // Hoy: solo PENDIENTE y CANCELADO
        estados.add(com.example.demo.model.EstadoCita.PENDIENTE);
        estados.add(com.example.demo.model.EstadoCita.CANCELADO);
    } else {
        // Pasado: todos los estados
        estados.add(com.example.demo.model.EstadoCita.PENDIENTE);
        estados.add(com.example.demo.model.EstadoCita.CANCELADO);
        estados.add(com.example.demo.model.EstadoCita.COMPLETADO);
        estados.add(com.example.demo.model.EstadoCita.NO_ASISTIO);
    }

    return estados;
}

@PostMapping("/cambiarEstadoCita")
public String cambiarEstadoCita(
        @RequestParam Long citaId,
        @RequestParam com.example.demo.model.EstadoCita nuevoEstado,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

    Long medicoId = (Long) session.getAttribute("medicoId");
    if (medicoId == null) {
        return "redirect:/login";
    }

    try {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (!citaOpt.isPresent() || !citaOpt.get().getMedicoId().equals(medicoId)) {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/gestionCitas";
        }

        Cita cita = citaOpt.get();

        // Validar que el estado sea permitido para esta fecha
        List<com.example.demo.model.EstadoCita> estadosPermitidos = getEstadosPermitidos(cita.getFecha());
        if (!estadosPermitidos.contains(nuevoEstado)) {
            redirectAttributes.addFlashAttribute("error",
                    "No puede cambiar a ese estado. La cita es " +
                            (cita.getFecha().isBefore(java.time.LocalDate.now()) ? "pasada" : "futura"));
            return "redirect:/gestionCitas";
        }

        // Actualizar estado
        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);

        redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado a: " + nuevoEstado.getDescripcion());
        return "redirect:/gestionCitas";

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        return "redirect:/gestionCitas";
    }
}
