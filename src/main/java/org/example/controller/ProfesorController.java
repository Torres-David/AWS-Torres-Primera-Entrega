package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Profesor;
import org.example.repository.ProfesorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profesores")
public class ProfesorController {

    private final ProfesorRepository profesorRepository;

    public ProfesorController(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    @GetMapping
    public ResponseEntity<List<Profesor>> getAll() {
        return ResponseEntity.ok(profesorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return profesorRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Profesor no encontrado con id: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Profesor profesor) {
        Profesor saved = profesorRepository.save(profesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Profesor profesorActualizado) {
        Optional<Profesor> opt = profesorRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Profesor no encontrado con id: " + id));
        }
        Profesor existing = opt.get();
        existing.setNombres(profesorActualizado.getNombres());
        existing.setApellidos(profesorActualizado.getApellidos());
        existing.setNumeroEmpleado(profesorActualizado.getNumeroEmpleado());
        existing.setHorasClase(profesorActualizado.getHorasClase());
        return ResponseEntity.ok(profesorRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!profesorRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Profesor no encontrado con id: " + id));
        }
        profesorRepository.deleteById(id);
        return ResponseEntity.ok(new ErrorResponse("Profesor eliminado correctamente"));
    }
}
