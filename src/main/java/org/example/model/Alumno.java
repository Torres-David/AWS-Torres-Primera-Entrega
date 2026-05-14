package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El campo nombres no debe estar vacío")
    private String nombres;

    @NotBlank(message = "El campo apellidos no debe estar vacío")
    private String apellidos;

    @NotBlank(message = "El campo matricula no debe estar vacío")
    private String matricula;

    @NotNull(message = "El campo promedio no debe estar vacío")
    @DecimalMin(value = "0.0", inclusive = true, message = "El promedio debe ser al menos 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "El promedio no debe ser mayor a 10.0")
    private Double promedio;

    private String fotoPerfilUrl;

    private String password;

    public Alumno() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public Double getPromedio() { return promedio; }
    public void setPromedio(Double promedio) { this.promedio = promedio; }

    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
