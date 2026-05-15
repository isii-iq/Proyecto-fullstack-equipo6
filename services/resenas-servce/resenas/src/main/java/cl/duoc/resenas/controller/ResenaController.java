package cl.duoc.resenas.controller;

import cl.duoc.resenas.dto.ResenaCreateDTO;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resenas")
public class ResenaController {

    @Autowired
    private ResenaService service;

    @GetMapping
    public ResponseEntity<List<ResenaDTO>> getAll() {

        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> getById(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody ResenaCreateDTO dto) {
       
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la reseña con ID: " + id));
    }
}