package clinica_backend.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

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

import clinica_backend.models.Paciente;
import clinica_backend.repositories.PacienteRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/paciente")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    @PostMapping(value = "/registrar", consumes = "application/json")
    public ResponseEntity<Map<String, String>> registrarPaciente(@RequestBody Paciente paciente) {
        try {
            // Validar si todos los campos obligatorios están completos
            if (paciente.getCorreo() == null || paciente.getContraseña() == null || 
                paciente.getNombres() == null || paciente.getApellidos() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Todos los campos son obligatorios."));
            }

            // Verificar si el correo ya está registrado
            if (pacienteRepository.findByCorreo(paciente.getCorreo()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "El correo ya está registrado. Intenta con otro."));
            }

            // Cifrar la contraseña antes de guardarla
            paciente.setContraseña(passwordEncoder.encode(paciente.getContraseña()));

            // Guardar el paciente en la base de datos
            pacienteRepository.save(paciente);

            // Generar un token JWT con el rol "PACIENTE"
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            String token = Jwts.builder()
                    .setSubject(paciente.getCorreo())
                    .claim("rol", "PACIENTE") // Agregar el rol al JWT
                    .setIssuedAt(new Date()) // Fecha de emisión
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // Crear un mapa con el token y la URL de redirección
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("redirectUrl", "/paginaprincipalPaciente.html");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Error en la longitud de la clave JWT: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Error en la longitud de la clave JWT. Revise la configuración."));
        } catch (Exception e) {
            // Agregar detalles del error para poder depurar
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error en el registro del paciente: " + e.getMessage()));
        }
    }

    @GetMapping("/datos/{correo}")
    public ResponseEntity<Paciente> obtenerPacientePorCorreo(@PathVariable String correo) {
        try {
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreo(correo);

            if (pacienteOpt.isPresent()) {
                return ResponseEntity.ok(pacienteOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Método para obtener los datos del paciente autenticado
    @GetMapping("/sesion")
    public ResponseEntity<?> obtenerPacienteDesdeSesion(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no presente en la solicitud.");
            }

            String token = authHeader.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String correo = claims.getSubject();
            String rol = claims.get("rol", String.class);

            // Verificar que el rol sea el esperado
            if (!"PACIENTE".equals(rol)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol no autorizado para esta operación.");
            }

            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreo(correo);
            if (pacienteOpt.isPresent()) {
                return ResponseEntity.ok(pacienteOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado.");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado. Por favor, inicia sesión nuevamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el token: " + e.getMessage());
        }
    }
}
