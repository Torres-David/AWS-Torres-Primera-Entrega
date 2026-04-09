package org.example.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class Profesor {

    private Long id;

    @NotNull(message = "El campo numeroEmpleado no debe estar vacío")
    @Min(value = 1, message = "El número de empleado debe ser mayor a 0")
    private Integer numeroEmpleado;

    @NotBlank(message = "El campo nombres no debe estar vacío")
    private String nombres;

    @NotBlank(message = "El campo apellidos no debe estar vacío")
    private String apellidos;

    @NotNull(message = "El campo horasClase no debe estar vacío")
    @Min(value = 0, message = "Las horas de clase no pueden ser negativas")
    private Integer horasClase;

    public Profesor() {
    }

    public Profesor(Long id, Integer numeroEmpleado, String nombres, String apellidos, Integer horasClase) {
        this.id = id;
        this.numeroEmpleado = numeroEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.horasClase = horasClase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(Integer numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Integer getHorasClase() {
        return horasClase;
    }

    public void setHorasClase(Integer horasClase) {
        this.horasClase = horasClase;
    }
}
