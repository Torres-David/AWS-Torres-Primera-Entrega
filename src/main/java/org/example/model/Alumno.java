package org.example.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class Alumno {

    private Long id;

    @NotBlank(message = "El campo nombres no debe estar vacío")
    private String nombres;

    @NotBlank(message = "El campo apellidos no debe estar vacío")
    private String apellidos;

    @NotBlank(message = "El campo matricula no debe estar vacío")
    private String matricula;

    @NotNull(message = "El campo promedio no debe estar vacío")
    @DecimalMin(value = "0.0", message = "El promedio debe ser al menos 0.0")
    @DecimalMax(value = "10.0", message = "El promedio no debe ser mayor a 10.0")
    private Double promedio;

    public Alumno() {
    }

    public Alumno(Long id, String nombres, String apellidos, String matricula, Double promedio) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.matricula = matricula;
        this.promedio = promedio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Double getPromedio() {
        return promedio;
    }

    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }
}
