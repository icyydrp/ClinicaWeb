package clinica_backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa una receta médica asociada a una cita.
 */
@Entity
@Table(name = "recetas")  // Especifica el nombre exacto de la tabla en la base de datos
public class Receta {

    /** Identificador único de la receta. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cita asociada a la receta, representa la relación con la tabla de citas. */
    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)  // Llave foránea hacia la tabla de citas
    private Cita cita;

    /** Medicamentos prescritos en la receta. */
    private String medicamentos;

    /** Dosis de los medicamentos prescritos. */
    private String dosis;

    /** Frecuencia con la que se deben administrar los medicamentos. */
    private String frecuencia;

    /** Instrucciones adicionales sobre el uso de los medicamentos. */
    private String instrucciones;

    /** Notas adicionales del médico sobre la receta. */
    private String notaAdicional;

    /** Fecha de emisión de la receta, con formato 'yyyy-MM-dd'. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String fechaEmision;

    // Getters y Setters

    /**
     * Obtiene el ID de la receta.
     * @return ID de la receta.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID de la receta.
     * @param id ID de la receta.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene la cita asociada a la receta.
     * @return Cita asociada.
     */
    public Cita getCita() {
        return cita;
    }

    /**
     * Establece la cita asociada a la receta.
     * @param cita Cita asociada.
     */
    public void setCita(Cita cita) {
        this.cita = cita;
    }

    /**
     * Obtiene los medicamentos prescritos en la receta.
     * @return Medicamentos prescritos.
     */
    public String getMedicamentos() {
        return medicamentos;
    }

    /**
     * Establece los medicamentos prescritos en la receta.
     * @param medicamentos Medicamentos prescritos.
     */
    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    /**
     * Obtiene la dosis de los medicamentos.
     * @return Dosis de los medicamentos.
     */
    public String getDosis() {
        return dosis;
    }

    /**
     * Establece la dosis de los medicamentos.
     * @param dosis Dosis de los medicamentos.
     */
    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    /**
     * Obtiene la frecuencia de administración de los medicamentos.
     * @return Frecuencia de administración.
     */
    public String getFrecuencia() {
        return frecuencia;
    }

    /**
     * Establece la frecuencia de administración de los medicamentos.
     * @param frecuencia Frecuencia de administración.
     */
    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    /**
     * Obtiene las instrucciones adicionales sobre el uso de los medicamentos.
     * @return Instrucciones adicionales.
     */
    public String getInstrucciones() {
        return instrucciones;
    }

    /**
     * Establece las instrucciones adicionales sobre el uso de los medicamentos.
     * @param instrucciones Instrucciones adicionales.
     */
    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    /**
     * Obtiene las notas adicionales del médico sobre la receta.
     * @return Nota adicional.
     */
    public String getNotaAdicional() {
        return notaAdicional;
    }

    /**
     * Establece las notas adicionales del médico sobre la receta.
     * @param notaAdicional Nota adicional.
     */
    public void setNotaAdicional(String notaAdicional) {
        this.notaAdicional = notaAdicional;
    }

    /**
     * Obtiene la fecha de emisión de la receta.
     * @return Fecha de emisión en formato 'yyyy-MM-dd'.
     */
    public String getFechaEmision() {
        return fechaEmision;
    }

    /**
     * Establece la fecha de emisión de la receta.
     * @param fechaEmision Fecha de emisión en formato 'yyyy-MM-dd'.
     */
    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
