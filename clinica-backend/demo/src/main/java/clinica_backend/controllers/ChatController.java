package clinica_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @GetMapping
    public ResponseEntity<String> iniciarChat(@RequestParam Integer pacienteId) {
        // Aquí puedes manejar la lógica para el chat.
        return ResponseEntity.ok("Chat iniciado con el paciente ID: " + pacienteId);
    }
}
