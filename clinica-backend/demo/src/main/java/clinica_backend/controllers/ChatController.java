package clinica_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.repositories.CitaRepository;

/**
 * Controlador para gestionar las operaciones relacionadas con los chats y citas.
 * Provee endpoints para obtener citas por médico y médicos relacionados a un paciente.
 */
@RestController
@RequestMapping("/api/chat") // Ruta consistente con el uso en JS
public class ChatController {

    @Autowired
    private CitaRepository citaRepository;

    /**
     * Obtiene todas las citas asociadas a un médico específico.
     *
     * @param medicoId ID del médico cuyas citas se desean obtener.
     * @return Una respuesta HTTP que contiene la lista de citas. Si no se encuentran citas, 
     *         devuelve un estado HTTP 404 (NOT_FOUND).
     */
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<Cita>> obtenerCitasPorMedico(@PathVariable Long medicoId) {
        System.out.println("ID del médico recibido: " + medicoId); // Log para verificar

        List<Cita> citas = citaRepository.findByMedico_Id(medicoId);

        if (citas.isEmpty()) {
            System.out.println("No se encontraron citas para el médico: " + medicoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        System.out.println("Citas encontradas: " + citas.size());
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtiene los médicos con los que un paciente tiene citas.
     *
     * @param pacienteId ID del paciente cuyas citas se desean consultar.
     * @return Una respuesta HTTP con la lista de médicos. Si no se encuentran médicos, 
     *         devuelve un estado HTTP 404 (NOT_FOUND).
     */
    @GetMapping("/paciente/{pacienteId}/medicos")
    public ResponseEntity<List<Medico>> obtenerMedicosPorPaciente(@PathVariable Long pacienteId) {
        List<Medico> medicos = citaRepository.findMedicosByPacienteId(pacienteId);
        if (medicos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(medicos);
    }
}
