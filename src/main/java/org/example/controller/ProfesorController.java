package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Profesor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/profesores")
public class ProfesorController {

    private final List<Profesor> profesores = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @GetMapping
    public ResponseEntity<List<Profesor>> getAll() {
        return ResponseEntity.ok(profesores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Profesor> profesor = profesores.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (profesor.isPresent()) {
            return ResponseEntity.ok(profesor.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Profesor no encontrado con id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Profesor profesor) {
        if (profesor.getId() == null || profesor.getId() == 0) {
            profesor.setId(idCounter.getAndIncrement());
        }
        profesores.add(profesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(profesor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Profesor profesorActualizado) {
        for (int i = 0; i < profesores.size(); i++) {
            if (profesores.get(i).getId().equals(id)) {
                profesorActualizado.setId(id);
                profesores.set(i, profesorActualizado);
                return ResponseEntity.ok(profesorActualizado);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Profesor no encontrado con id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean removed = profesores.removeIf(p -> p.getId().equals(id));
        if (removed) {
            return ResponseEntity.ok(new ErrorResponse("Profesor eliminado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Profesor no encontrado con id: " + id));
    }
}
