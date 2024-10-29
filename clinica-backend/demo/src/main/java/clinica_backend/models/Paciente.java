package clinica_backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Representa un paciente dentro del sistema de gestión de la clínica.
 */
@Entity
@Table(name = "pacientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Paciente {

    /** Identificador único del paciente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo electrónico del paciente, debe ser único. */
    @Column(length = 50, nullable = false, unique = true)
    private String correo;

    /** Contraseña del paciente para acceso al sistema. */
    @Column(length = 255, nullable = false)
    private String contraseña;

    /** Nombres del paciente. */
    @Column(length = 50, nullable = false)
    private String nombres;

    /** Apellidos del paciente. */
    @Column(length = 50, nullable = false)
    private String apellidos;

    /** Número de celular del paciente. */
    @Column(length = 20)
    private String celular;

    /** Documento Nacional de Identidad (DNI) del paciente. */
    @Column(length = 10)
    private String dni;

    /** Foto del paciente. */
    @Column(length = 255)
    private String foto;

    /** Lista de citas asociadas al paciente. */
    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Cita> citas;

    // Getters y Setters

    /**
     * Obtiene el ID del paciente.
     * @return ID del paciente.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del paciente.
     * @param id ID del paciente.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el correo electrónico del paciente.
     * @return Correo del paciente.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del paciente.
     * @param correo Correo del paciente.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la contraseña del paciente.
     * @return Contraseña del paciente.
     */
    public String getContraseña() {
        return contraseña;
    }

    /**
     * Establece la contraseña del paciente.
     * @param contraseña Contraseña del paciente.
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    /**
     * Obtiene los nombres del paciente.
     * @return Nombres del paciente.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres del paciente.
     * @param nombres Nombres del paciente.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Obtiene los apellidos del paciente.
     * @return Apellidos del paciente.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del paciente.
     * @param apellidos Apellidos del paciente.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el número de celular del paciente.
     * @return Número de celular del paciente.
     */
    public String getCelular() {
        return celular;
    }

    /**
     * Establece el número de celular del paciente.
     * @param celular Número de celular del paciente.
     */
    public void setCelular(String celular) {
        this.celular = celular;
    }

    /**
     * Obtiene el DNI del paciente.
     * @return DNI del paciente.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del paciente.
     * @param dni DNI del paciente.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene la foto del paciente.
     * @return Foto del paciente.
     */
    public String getFoto() {
        return foto;
    }

    /**
     * Establece la foto del paciente.
     * @param foto Foto del paciente.
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }

    /**
     * Obtiene la lista de citas asociadas al paciente.
     * @return Lista de citas del paciente.
     */
    public List<Cita> getCitas() {
        return citas;
    }

    /**
     * Establece la lista de citas asociadas al paciente.
     * @param citas Lista de citas del paciente.
     */
    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }
}
