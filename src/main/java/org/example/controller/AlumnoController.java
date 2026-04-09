package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Alumno;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    private final List<Alumno> alumnos = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @GetMapping
    public ResponseEntity<List<Alumno>> getAll() {
        return ResponseEntity.ok(alumnos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Alumno> alumno = alumnos.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();

        if (alumno.isPresent()) {
            return ResponseEntity.ok(alumno.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Alumno no encontrado con id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Alumno alumno) {
        alumno.setId(idCounter.getAndIncrement());
        alumnos.add(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(alumno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Alumno alumnoActualizado) {
        for (int i = 0; i < alumnos.size(); i++) {
            if (alumnos.get(i).getId().equals(id)) {
                alumnoActualizado.setId(id);
                alumnos.set(i, alumnoActualizado);
                return ResponseEntity.ok(alumnoActualizado);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Alumno no encontrado con id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean removed = alumnos.removeIf(a -> a.getId().equals(id));
        if (removed) {
            return ResponseEntity.ok(new ErrorResponse("Alumno eliminado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Alumno no encontrado con id: " + id));
    }
}
