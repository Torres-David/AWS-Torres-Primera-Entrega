package org.example.repository;

import org.example.model.SesionAlumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SesionAlumnoRepository extends JpaRepository<SesionAlumno, String> {
    Optional<SesionAlumno> findByAlumnoIdAndSessionString(Long alumnoId, String sessionString);
}
