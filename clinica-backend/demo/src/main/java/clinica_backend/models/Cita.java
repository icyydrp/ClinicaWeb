package clinica_backend.models;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa una cita médica dentro del sistema.
 */
@Entity
@Table(name = "citas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cita {

    /** Identificador único de la cita. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Paciente asociado a la cita. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Médico responsable de la cita.
     * Ignora las propiedades perezosas de Hibernate y evita referencias circulares.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "citas"})
    private Medico medico;

    /** Fecha de la cita. */
    @Column(nullable = false)
    private Date fecha;

    /** Hora de la cita. */
    @Column(nullable = false)
    private Time hora;

    /** Motivo de la cita. */
    @Column(length = 255)
    private String motivo;

    /** Estado de la cita (e.g., 'pendiente', 'confirmada', 'cancelada'). */
    @Column(length = 20)
    private String estado;

    /** Fecha y hora en que se creó la cita. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    /** Especialidad médica de la cita. */
    @Column(length = 255, nullable = false)
    private String especialidad;

    /** Comentario adicional sobre la cita. */
    @Column(length = 500)
    private String comentario;

    // Getters y Setters

    /**
     * Obtiene el ID de la cita.
     * @return ID de la cita.
     */
    public Long getId() { return id; }

    /**
     * Establece el ID de la cita.
     * @param id ID de la cita.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el paciente asociado a la cita.
     * @return Paciente de la cita.
     */
    public Paciente getPaciente() { return paciente; }

    /**
     * Establece el paciente para la cita.
     * @param paciente Paciente asociado.
     */
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    /**
     * Obtiene el médico responsable de la cita.
     * @return Médico de la cita.
     */
    public Medico getMedico() { return medico; }

    /**
     * Establece el médico para la cita.
     * @param medico Médico responsable.
     */
    public void setMedico(Medico medico) { this.medico = medico; }

    /**
     * Obtiene la fecha de la cita.
     * @return Fecha de la cita.
     */
    public Date getFecha() { return fecha; }

    /**
     * Establece la fecha de la cita.
     * @param fecha Fecha de la cita.
     */
    public void setFecha(Date fecha) { this.fecha = fecha; }

    /**
     * Obtiene la hora de la cita.
     * @return Hora de la cita.
     */
    public Time getHora() { return hora; }

    /**
     * Establece la hora de la cita.
     * @param hora Hora de la cita.
     */
    public void setHora(Time hora) { this.hora = hora; }

    /**
     * Obtiene el motivo de la cita.
     * @return Motivo de la cita.
     */
    public String getMotivo() { return motivo; }

    /**
     * Establece el motivo de la cita.
     * @param motivo Motivo de la cita.
     */
    public void setMotivo(String motivo) { this.motivo = motivo; }

    /**
     * Obtiene el estado de la cita.
     * @return Estado de la cita.
     */
    public String getEstado() { return estado; }

    /**
     * Establece el estado de la cita.
     * @param estado Estado de la cita.
     */
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Obtiene la fecha de creación de la cita.
     * @return Fecha de creación.
     */
    public Timestamp getCreatedAt() { return createdAt; }

    /**
     * Establece la fecha de creación de la cita.
     * @param createdAt Fecha de creación.
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Obtiene la especialidad médica de la cita.
     * @return Especialidad médica.
     */
    public String getEspecialidad() { return especialidad; }

    /**
     * Establece la especialidad médica de la cita.
     * @param especialidad Especialidad médica.
     */
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    /**
     * Obtiene el comentario adicional sobre la cita.
     * @return Comentario adicional.
     */
    public String getComentario() { return comentario; }

    /**
     * Establece un comentario adicional para la cita.
     * @param comentario Comentario adicional.
     */
    public void setComentario(String comentario) { this.comentario = comentario; }
}
