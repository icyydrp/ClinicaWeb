package clinica_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Configuración de Jackson para la serialización y deserialización de objetos JSON.
 * Esta clase define cómo se comportará Jackson al manejar objetos en la aplicación.
 */
@Configuration
public class JacksonConfig {

    /**
     * Crea y configura un bean de tipo {@link ObjectMapper}.
     *
     * @return Un objeto {@link ObjectMapper} configurado para la serialización JSON.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Configura para no fallar en la serialización de objetos vacíos.
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return objectMapper;
    }
}
