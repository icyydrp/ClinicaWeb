package clinica_backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.models.Medico;
import clinica_backend.repositories.MedicoRepository;
@RestController
@RequestMapping("/reportes/medicos")
public class MedicosReporteController {

    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping
    public List<Map<String, Object>> generarReporteMedicos() {
        List<Medico> medicos = medicoRepository.findAll();

        return medicos.stream().map(medico -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", medico.getId());
            data.put("nombre", medico.getNombres() + " " + medico.getApellidos());
            data.put("especialidad", medico.getEspecialidad() != null ? medico.getEspecialidad() : "No asignada");
            data.put("numeroColegiatura", medico.getNumeroColegiatura() != null ? medico.getNumeroColegiatura() : "No registrado");
            data.put("correo", medico.getCorreo() != null ? medico.getCorreo() : "No registrado");
            return data;
        }).collect(Collectors.toList());
    }
}
