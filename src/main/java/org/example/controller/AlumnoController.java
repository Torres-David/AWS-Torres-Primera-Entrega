package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Alumno;
import org.example.model.SesionAlumno;
import org.example.repository.AlumnoRepository;
import org.example.repository.SesionAlumnoRepository;
import org.example.service.S3Service;
import org.example.service.SnsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AlumnoRepository alumnoRepository;
    private final SesionAlumnoRepository sesionAlumnoRepository;
    private final SnsService snsService;
    private final S3Service s3Service;

    public AlumnoController(AlumnoRepository alumnoRepository,
                            SesionAlumnoRepository sesionAlumnoRepository,
                            SnsService snsService,
                            S3Service s3Service) {
        this.alumnoRepository = alumnoRepository;
        this.sesionAlumnoRepository = sesionAlumnoRepository;
        this.snsService = snsService;
        this.s3Service = s3Service;
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Alumno>> getAll() {
        return ResponseEntity.ok(alumnoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return alumnoRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Alumno no encontrado con id: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Alumno alumno) {
        Alumno saved = alumnoRepository.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Alumno alumnoActualizado) {
        Optional<Alumno> opt = alumnoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Alumno no encontrado con id: " + id));
        }
        Alumno existing = opt.get();
        existing.setNombres(alumnoActualizado.getNombres());
        existing.setApellidos(alumnoActualizado.getApellidos());
        existing.setMatricula(alumnoActualizado.getMatricula());
        existing.setPromedio(alumnoActualizado.getPromedio());
        if (alumnoActualizado.getFotoPerfilUrl() != null) existing.setFotoPerfilUrl(alumnoActualizado.getFotoPerfilUrl());
        if (alumnoActualizado.getPassword() != null) existing.setPassword(alumnoActualizado.getPassword());
        return ResponseEntity.ok(alumnoRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!alumnoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Alumno no encontrado con id: " + id));
        }
        alumnoRepository.deleteById(id);
        return ResponseEntity.ok(new ErrorResponse("Alumno eliminado correctamente"));
    }

    // ── Foto de perfil ───────────────────────────────────────────────────────

    @PostMapping(value = "/{id}/fotoPerfil", consumes = "multipart/form-data")
    public ResponseEntity<?> actualizarFotoPerfil(@PathVariable Long id,
                                                   @RequestParam("foto") MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("El archivo de foto es obligatorio"));
        }
        Optional<Alumno> optFoto = alumnoRepository.findById(id);
        if (optFoto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Alumno no encontrado con id: " + id));
        }
        try {
            String url = s3Service.subirFoto(id, foto);
            Alumno alumno = optFoto.get();
            alumno.setFotoPerfilUrl(url);
            return ResponseEntity.ok(alumnoRepository.save(alumno));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al subir la foto: " + e.getMessage()));
        }
    }

    // ── Envío de correo via SNS ──────────────────────────────────────────────

    @PostMapping("/{id}/email")
    public ResponseEntity<?> enviarEmail(@PathVariable Long id) {
        Optional<Alumno> opt = alumnoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Alumno no encontrado con id: " + id));
        }
        Alumno alumno = opt.get();
        String mensaje = String.format(
                "Información del alumno:\nNombre: %s %s\nMatrícula: %s\nPromedio: %.2f",
                alumno.getNombres(), alumno.getApellidos(),
                alumno.getMatricula(), alumno.getPromedio());
        snsService.publicarMensaje("Calificaciones del alumno", mensaje);
        return ResponseEntity.ok(new ErrorResponse("Correo enviado correctamente"));
    }

    // ── Sesiones ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/session/login")
    public ResponseEntity<?> login(@PathVariable Long id,
                                   @RequestBody Map<String, String> body) {
        String password = body.get("password");
        Optional<Alumno> opt = alumnoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Alumno no encontrado con id: " + id));
        }
        Alumno alumno = opt.get();
        if (alumno.getPassword() == null || !alumno.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Contraseña incorrecta"));
        }
        SesionAlumno sesion = new SesionAlumno();
        sesion.setId(UUID.randomUUID().toString());
        sesion.setFecha(System.currentTimeMillis() / 1000L);
        sesion.setAlumnoId(id);
        sesion.setActive(true);
        sesion.setSessionString(generarSessionString(128));
        sesionAlumnoRepository.save(sesion);
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/{id}/session/verify")
    public ResponseEntity<?> verify(@PathVariable Long id,
                                    @RequestBody Map<String, String> body) {
        String sessionString = body.get("sessionString");
        Optional<SesionAlumno> opt = sesionAlumnoRepository
                .findByAlumnoIdAndSessionString(id, sessionString);
        if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getActive())) {
            return ResponseEntity.ok(new ErrorResponse("Sesión válida"));
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Sesión inválida o inactiva"));
    }

    @PostMapping("/{id}/session/logout")
    public ResponseEntity<?> logout(@PathVariable Long id,
                                    @RequestBody Map<String, String> body) {
        String sessionString = body.get("sessionString");
        Optional<SesionAlumno> opt = sesionAlumnoRepository
                .findByAlumnoIdAndSessionString(id, sessionString);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Sesión no encontrada"));
        }
        SesionAlumno sesion = opt.get();
        sesion.setActive(false);
        sesionAlumnoRepository.save(sesion);
        return ResponseEntity.ok(new ErrorResponse("Sesión cerrada correctamente"));
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private String generarSessionString(int longitud) {
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
