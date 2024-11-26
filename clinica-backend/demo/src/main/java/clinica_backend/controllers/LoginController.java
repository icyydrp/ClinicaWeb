package clinica_backend.controllers;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.PacienteRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Value("${jwt.secret}")
    private String secret;

    @PostMapping
    public ResponseEntity<Map<String, String>> iniciarSesion(@RequestParam String correo, @RequestParam String contraseña) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contraseña)
            );

            // Determinar el rol del usuario
            String rol;
            String redirectUrl;
            
            if (medicoRepository.findByCorreo(correo).isPresent()) {
                rol = "MEDICO";
                redirectUrl = "/paginaprincipalMedico.html";
            } else if (pacienteRepository.findByCorreo(correo).isPresent()) {
                rol = "PACIENTE";
                redirectUrl = "/paginaprincipalPaciente.html";
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Usuario no encontrado"));
            }

            // Generar token JWT incluyendo el rol
            Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
            String jwt = Jwts.builder()
                    .setSubject(correo)
                    .claim("role", rol)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // Crear respuesta con el token y la URL de redirección
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("redirectUrl", redirectUrl);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Credenciales inválidas"));
        }
    }
}