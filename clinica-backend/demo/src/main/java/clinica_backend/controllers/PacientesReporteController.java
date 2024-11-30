package clinica_backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.models.Paciente;
import clinica_backend.repositories.PacienteRepository;

@RestController
@RequestMapping("/reportes/pacientes")
public class PacientesReporteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping
    public List<Map<String, Object>> generarReportePacientes() {
        List<Paciente> pacientes = pacienteRepository.findAll();

        return pacientes.stream().map(paciente -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", paciente.getId());
            data.put("nombre", paciente.getNombres() + " " + paciente.getApellidos());
            data.put("correo", paciente.getCorreo() != null ? paciente.getCorreo() : "No registrado");
            data.put("celular", paciente.getCelular() != null ? paciente.getCelular() : "No registrado");
            data.put("dni", paciente.getDni() != null ? paciente.getDni() : "No registrado");
            return data;
        }).collect(Collectors.toList());
    }
}
