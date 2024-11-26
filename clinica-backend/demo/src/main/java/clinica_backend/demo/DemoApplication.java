package clinica_backend.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de la aplicación que inicia la ejecución de la aplicación
 * Spring Boot.
 * Esta clase configura los paquetes base para los componentes, entidades y
 * repositorios.
 */
@SpringBootApplication(scanBasePackages = "clinica_backend")
@EnableJpaRepositories(basePackages = "clinica_backend.repositories")
@EntityScan(basePackages = "clinica_backend.models") // Escanea las entidades en el paquete especificado
public class DemoApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos pasados desde la línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
