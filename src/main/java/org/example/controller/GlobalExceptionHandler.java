package org.example.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        response.put("mensaje", "Error de validación");
        response.put("errores", errores);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            response.put("mensaje", "Tipo de dato incorrecto en el campo: " +
                    ife.getPath().stream()
                            .map(JsonMappingException.Reference::getFieldName)
                            .reduce("", (a, b) -> a.isEmpty() ? b : a + "." + b));
            response.put("detalle", ife.getOriginalMessage());
        } else if (cause instanceof JsonMappingException jme) {
            response.put("mensaje", "Error de tipo de dato: " +
                    jme.getPath().stream()
                            .map(JsonMappingException.Reference::getFieldName)
                            .reduce("", (a, b) -> a.isEmpty() ? b : a + "." + b));
            response.put("detalle", jme.getOriginalMessage());
        } else {
            response.put("mensaje", "Error en el formato del JSON o tipo de dato incorrecto");
            response.put("detalle", ex.getMostSpecificCause().getMessage());
        }

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Error interno del servidor");
        response.put("detalle", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
