package clinica_backend.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private static final String BACKUP_FILE_NAME = "backup.sql";
    private static final String RESTORE_FILE_NAME = "restore.sql";
    private static final String DB_USER = "usuario"; // Cambiar por usuario real
    private static final String DB_NAME = "base_de_datos"; // Cambiar por base real

    /**
     * Descargar copia de seguridad.
     */
  @GetMapping("/descargar-backup")
public ResponseEntity<InputStreamResource> descargarBackup() {
    try {
        String command = String.format("pg_dump -U %s -d %s -F c -f %s", DB_USER, DB_NAME, BACKUP_FILE_NAME);
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            // Leer el error del proceso
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            StringBuilder errorMessage = new StringBuilder();
            while ((errorLine = errorReader.readLine()) != null) {
                errorMessage.append(errorLine).append("\n");
            }
            System.err.println("Error en pg_dump: " + errorMessage.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }

        File backupFile = new File(BACKUP_FILE_NAME);
        if (!backupFile.exists()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(backupFile));
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + BACKUP_FILE_NAME)
                             .contentType(MediaType.APPLICATION_OCTET_STREAM)
                             .body(resource);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

    /**
     * Restaurar copia de seguridad.
     */
    @PostMapping("/restaurar-backup")
    public ResponseEntity<String> restaurarBackup(@RequestParam("backupFile") MultipartFile backupFile) {
        try {
            File file = new File(RESTORE_FILE_NAME);
            backupFile.transferTo(file);

            String command = String.format("psql -U %s -d %s -f %s", DB_USER, DB_NAME, RESTORE_FILE_NAME);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            if (process.exitValue() != 0) {
                return ResponseEntity.status(500).body("Error al restaurar la base de datos.");
            }

            return ResponseEntity.ok("Base de datos restaurada con éxito.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al restaurar la base de datos.");
        }
    }
}