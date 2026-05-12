package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sesiones_alumnos")
public class SesionAlumno {

    @Id
    private String id; // UUID string

    private Long fecha; // Unix timestamp

    private Long alumnoId;

    private Boolean active;

    @Column(length = 256)
    private String sessionString;

    public SesionAlumno() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getFecha() { return fecha; }
    public void setFecha(Long fecha) { this.fecha = fecha; }

    public Long getAlumnoId() { return alumnoId; }
    public void setAlumnoId(Long alumnoId) { this.alumnoId = alumnoId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getSessionString() { return sessionString; }
    public void setSessionString(String sessionString) { this.sessionString = sessionString; }
}
