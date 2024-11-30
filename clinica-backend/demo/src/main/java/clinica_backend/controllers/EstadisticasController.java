package clinica_backend.controllers;

import clinica_backend.repositories.CitaRepository;
import clinica_backend.models.Cita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticasController {

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping("/citas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasCitas() {
        try {
            List<Cita> citas = citaRepository.findAll();

            Map<String, Integer> citasPorMes = new HashMap<>();
            Map<String, Integer> estados = new HashMap<>();
            Map<String, Integer> especialidades = new HashMap<>();

            for (Cita cita : citas) {
                // Obtener el mes
                String mes = (cita.getFecha() != null)
                        ? cita.getFecha().toLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
                        : "Desconocido";

                // Obtener el estado
                String estado = (cita.getEstado() != null) ? cita.getEstado() : "Desconocido";

                // Obtener la especialidad
                String especialidad = (cita.getEspecialidad() != null) ? cita.getEspecialidad() : "Sin especialidad";

                // Incrementar contadores
                citasPorMes.put(mes, citasPorMes.getOrDefault(mes, 0) + 1);
                estados.put(estado, estados.getOrDefault(estado, 0) + 1);
                especialidades.put(especialidad, especialidades.getOrDefault(especialidad, 0) + 1);
            }

            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("meses", citasPorMes);
            response.put("estados", estados);
            response.put("especialidades", especialidades);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
