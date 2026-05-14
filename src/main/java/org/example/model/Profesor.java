package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "profesores")
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El campo numeroEmpleado no debe estar vacío")
    @DecimalMin(value = "1.0", inclusive = true, message = "El número de empleado debe ser mayor a 0")
    private Integer numeroEmpleado;

    @NotBlank(message = "El campo nombres no debe estar vacío")
    private String nombres;

    @NotBlank(message = "El campo apellidos no debe estar vacío")
    private String apellidos;

    @NotNull(message = "El campo horasClase no debe estar vacío")
    @DecimalMin(value = "0.0", inclusive = true, message = "Las horas de clase no pueden ser negativas")
    private Integer horasClase;

    public Profesor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumeroEmpleado() { return numeroEmpleado; }
    public void setNumeroEmpleado(Integer numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Integer getHorasClase() { return horasClase; }
    public void setHorasClase(Integer horasClase) { this.horasClase = horasClase; }
}
