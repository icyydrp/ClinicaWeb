package clinica_backend.models;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Representa un mensaje de chat en el sistema.
 * Cada mensaje está asociado a una cita y contiene información
 * sobre el remitente, el mensaje enviado y la fecha de envío.
 */
@Entity
public class Chat {

    /**
     * Identificador único del mensaje de chat.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Remitente del mensaje.
     */
    private String remitente;

    /**
     * Contenido del mensaje.
     */
    private String mensaje;

    /**
     * Fecha y hora en que se envió el mensaje.
     */
    private Timestamp fechaEnvio;

    /**
     * Cita a la que está asociado el mensaje de chat.
     */
    @ManyToOne
    private Cita cita;

    /**
     * Constructor vacío para la clase Chat.
     */
    public Chat() {}

    /**
     * Obtiene el ID del mensaje de chat.
     * 
     * @return el ID del mensaje.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el ID del mensaje de chat.
     * 
     * @param id el nuevo ID del mensaje.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el remitente del mensaje.
     * 
     * @return el nombre del remitente.
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Establece el remitente del mensaje.
     * 
     * @param remitente el nombre del remitente.
     */
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    /**
     * Obtiene el contenido del mensaje.
     * 
     * @return el mensaje enviado.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el contenido del mensaje.
     * 
     * @param mensaje el nuevo contenido del mensaje.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene la fecha y hora en que se envió el mensaje.
     * 
     * @return la fecha y hora del envío.
     */
    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * Establece la fecha y hora en que se envió el mensaje.
     * 
     * @param fechaEnvio la nueva fecha y hora de envío.
     */
    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    /**
     * Obtiene la cita asociada al mensaje de chat.
     * 
     * @return la cita asociada.
     */
    public Cita getCita() {
        return cita;
    }

    /**
     * Establece la cita asociada al mensaje de chat.
     * 
     * @param cita la nueva cita asociada.
     */
    public void setCita(Cita cita) {
        this.cita = cita;
    }
}
