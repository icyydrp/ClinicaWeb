package clinica_backend.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "clinica_backend")
@EnableJpaRepositories(basePackages = "clinica_backend.repositories")
@EntityScan(basePackages = "clinica_backend.models")  // Agrega esta línea para escanear las entidades
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
