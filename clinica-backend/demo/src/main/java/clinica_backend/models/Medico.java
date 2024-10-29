package clinica_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa un médico dentro del sistema de gestión de la clínica.
 */
@Entity
@Table(name = "medicos")
public class Medico {

    /** Identificador único del médico. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombres del médico. */
    private String nombres;

    /** Apellidos del médico. */
    private String apellidos;

    /** Correo electrónico del médico. */
    private String correo;

    /** Contraseña del médico para el inicio de sesión. */
    private String contraseña;

    /** 
     * Número de colegiatura del médico, debe ser único y no puede ser nulo. 
     */
    @Column(length = 20, unique = true, nullable = false)
    private String numeroColegiatura;

    /** Especialidad médica del médico. */
    private String especialidad;

    /** DNI del médico. */
    private String dni;

    /** URL o ruta de la foto del médico. */
    private String foto;

    // Getters y Setters

    /**
     * Obtiene el ID del médico.
     * @return ID del médico.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del médico.
     * @param id ID del médico.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene los nombres del médico.
     * @return Nombres del médico.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres del médico.
     * @param nombres Nombres del médico.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Obtiene los apellidos del médico.
     * @return Apellidos del médico.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del médico.
     * @param apellidos Apellidos del médico.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el correo electrónico del médico.
     * @return Correo electrónico del médico.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del médico.
     * @param correo Correo electrónico del médico.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la contraseña del médico.
     * @return Contraseña del médico.
     */
    public String getContraseña() {
        return contraseña;
    }

    /**
     * Establece la contraseña del médico.
     * @param contraseña Contraseña del médico.
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    /**
     * Obtiene el número de colegiatura del médico.
     * @return Número de colegiatura.
     */
    public String getNumeroColegiatura() {
        return numeroColegiatura;
    }

    /**
     * Establece el número de colegiatura del médico.
     * @param numeroColegiatura Número de colegiatura.
     */
    public void setNumeroColegiatura(String numeroColegiatura) {
        this.numeroColegiatura = numeroColegiatura;
    }

    /**
     * Obtiene la especialidad del médico.
     * @return Especialidad del médico.
     */
    public String getEspecialidad() {
        return especialidad;
    }

    /**
     * Establece la especialidad del médico.
     * @param especialidad Especialidad del médico.
     */
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    /**
     * Obtiene el DNI del médico.
     * @return DNI del médico.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del médico.
     * @param dni DNI del médico.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene la foto del médico.
     * @return URL o ruta de la foto.
     */
    public String getFoto() {
        return foto;
    }

    /**
     * Establece la foto del médico.
     * @param foto URL o ruta de la foto.
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }
}
