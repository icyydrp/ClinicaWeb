package clinica_backend.models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa una notificación dentro del sistema de gestión de la clínica.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    /** Identificador único de la notificación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Paciente asociado a la notificación. */
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /** Médico asociado a la notificación, si aplica. */
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    /** Mensaje de la notificación. */
    @Column(length = 255, nullable = false)
    private String mensaje;

    /** Indica si la notificación ha sido leída. */
    @Column(nullable = false)
    private boolean leido;

    /** Fecha y hora en que se creó la notificación. */
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    // Getters y Setters

    /**
     * Obtiene el ID de la notificación.
     * @return ID de la notificación.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID de la notificación.
     * @param id ID de la notificación.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el paciente asociado a la notificación.
     * @return Paciente asociado.
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Establece el paciente asociado a la notificación.
     * @param paciente Paciente asociado.
     */
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    /**
     * Obtiene el médico asociado a la notificación.
     * @return Médico asociado.
     */
    public Medico getMedico() {
        return medico;
    }

    /**
     * Establece el médico asociado a la notificación.
     * @param medico Médico asociado.
     */
    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    /**
     * Obtiene el mensaje de la notificación.
     * @return Mensaje de la notificación.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje de la notificación.
     * @param mensaje Mensaje de la notificación.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Verifica si la notificación ha sido leída.
     * @return {@code true} si la notificación ha sido leída, de lo contrario {@code false}.
     */
    public boolean isLeido() {
        return leido;
    }

    /**
     * Establece el estado de lectura de la notificación.
     * @param leido {@code true} si la notificación ha sido leída, de lo contrario {@code false}.
     */
    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    /**
     * Obtiene la fecha y hora en que se creó la notificación.
     * @return Fecha y hora de creación.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha y hora en que se creó la notificación.
     * @param createdAt Fecha y hora de creación.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
