package clinica_backend.controllers;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.repositories.CitaRepository;
import clinica_backend.repositories.MedicoRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Controlador para gestionar las acciones relacionadas con los médicos.
 */
@RestController
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Registra un nuevo médico y devuelve un token JWT y la URL de redirección.
     *
     * @param medico Datos del médico a registrar.
     * @return Token JWT y URL de redirección si el registro es exitoso, o un mensaje de error en caso de fallo.
     */
    @PostMapping(value = "/registrar", consumes = "application/json")
    public ResponseEntity<Map<String, String>> registrarMedico(@RequestBody Medico medico) {
        try {
            // Verificar si el correo ya está registrado
            if (medicoRepository.findByCorreo(medico.getCorreo()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "El correo ya está registrado. Intenta con otro."));
            }

            // Cifrar la contraseña antes de guardarla
            medico.setContraseña(passwordEncoder.encode(medico.getContraseña()));

            // Guardar el médico en la base de datos
            medicoRepository.save(medico);

            // Generar un token JWT
            Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
            String token = Jwts.builder()
                    .setSubject(medico.getCorreo())
                    .claim("rol", "MEDICO") // Agregar el rol al JWT
                    .setIssuedAt(new Date()) // Ajuste aquí a java.util.Date
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // Crear un mapa con el token y la URL de redirección
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("redirectUrl", "/paginaprincipalMedico.html");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Añadir para debug
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Error en el registro del médico: " + e.getMessage()));
        }
    }

    /**
     * Obtiene las citas asignadas a un médico específico.
     *
     * @param medicoId ID del médico cuyas citas se desean obtener.
     * @return Lista de citas del médico.
     */
    @GetMapping("/citas/{medicoId}")
    public ResponseEntity<List<Cita>> obtenerCitasPorMedico(@PathVariable Long medicoId) {
        List<Cita> citas = citaRepository.findByMedico_Id(medicoId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtiene los datos del médico autenticado a través de su correo.
     *
     * @param correo El correo del médico.
     * @return ResponseEntity con el objeto Medico si existe.
     */
    @GetMapping("/datos/{correo}")
    public ResponseEntity<Medico> obtenerMedicoPorCorreo(@PathVariable String correo) {
        Optional<Medico> medicoOpt = medicoRepository.findByCorreo(correo);
        return medicoOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}