package clinica_backend.controllers;

import clinica_backend.models.Receta;
import clinica_backend.repositories.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/recetas") // Base endpoint para todas las operaciones relacionadas con recetas
public class RecetaController {

    @Autowired
    private RecetaRepository recetaRepository;

    /**
     * Obtiene todas las recetas disponibles.
     *
     * @return Lista de todas las recetas.
     */
    @GetMapping
    public ResponseEntity<List<Receta>> obtenerTodasLasRecetas() {
        List<Receta> recetas = recetaRepository.findAll();
        if (recetas.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna un 204 si no hay recetas
        }
        return ResponseEntity.ok(recetas); // Retorna las recetas con un 200 OK
    }

    /**
     * Obtiene una receta específica por su ID.
     *
     * @param id ID de la receta.
     * @return Receta encontrada, si existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerRecetaPorId(@PathVariable Long id) {
        return recetaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Retorna un 404 si no se encuentra la receta
    }

    /**
     * Obtiene todas las recetas asociadas a un paciente por su ID.
     *
     * @param pacienteId ID del paciente.
     * @return Lista de recetas del paciente.
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Receta>> obtenerRecetasPorPaciente(@PathVariable Long pacienteId) {
        List<Receta> recetas = recetaRepository.findByCita_Paciente_Id(pacienteId);
        if (recetas.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna un 204 si no hay recetas para el paciente
        }
        return ResponseEntity.ok(recetas);
    }

    /**
     * Obtiene todas las recetas asociadas a un médico por su ID.
     *
     * @param medicoId ID del médico.
     * @return Lista de recetas del médico.
     */
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<Receta>> obtenerRecetasPorMedico(@PathVariable Long medicoId) {
        List<Receta> recetas = recetaRepository.findByCita_Medico_Id(medicoId);
        if (recetas.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna un 204 si no hay recetas para el médico
        }
        return ResponseEntity.ok(recetas);
    }

    /**
     * Elimina una receta específica por su ID.
     *
     * @param id ID de la receta a eliminar.
     * @return Mensaje de confirmación o error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarReceta(@PathVariable Long id) {
        if (!recetaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Receta no encontrada con el ID especificado"));
        }
        recetaRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Receta eliminada exitosamente"));
    }
}
