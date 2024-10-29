package clinica_backend.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador para gestionar la entrega de archivos desde el servidor.
 * Permite acceder a los archivos subidos a través de una ruta específica.
 */
@Controller
public class FileController {   

    /**
     * Sirve un archivo al cliente según el nombre proporcionado en la ruta.
     *
     * @param filename Nombre del archivo que se desea descargar o visualizar.
     * @return Una respuesta HTTP con el archivo solicitado, o un error en caso de que no se pueda leer.
     * @throws RuntimeException Si el archivo no se encuentra o no es legible.
     */
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // Resuelve la ruta del archivo dentro del directorio "uploads"
            Path file = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            // Verifica si el archivo existe y es legible
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (Exception e) {
            // Maneja cualquier excepción relacionada con la entrega del archivo
            throw new RuntimeException("Error al servir el archivo", e);
        }
    }
}
