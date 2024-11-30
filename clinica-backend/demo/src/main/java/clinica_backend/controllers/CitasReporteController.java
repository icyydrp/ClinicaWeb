package clinica_backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.models.Cita;
import clinica_backend.repositories.CitaRepository;
@RestController
@RequestMapping("/reportes/citas")
public class CitasReporteController {

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping
    public List<Map<String, Object>> generarReporteCitas() {
        List<Cita> citas = citaRepository.findAll();

        return citas.stream().map(cita -> {
            Map<String, Object> data = new HashMap<>();
            data.put("idCita", cita.getId());
            data.put("paciente", cita.getPaciente() != null ? cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidos() : "No asignado");
            data.put("medico", cita.getMedico() != null ? cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos() : "No asignado");
            data.put("especialidad", cita.getMedico() != null && cita.getMedico().getEspecialidad() != null ? cita.getMedico().getEspecialidad() : "No asignada");
            data.put("fecha", cita.getFecha() != null ? cita.getFecha().toString() : "No especificada");
            data.put("hora", cita.getHora() != null ? cita.getHora().toString() : "No especificada");
            data.put("estado", cita.getEstado() != null ? cita.getEstado() : "No definido");
            return data;
        }).collect(Collectors.toList());
    }
}
