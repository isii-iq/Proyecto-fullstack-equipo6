package cl.duoc.resenas.controller;

import cl.duoc.resenas.dto.ResenaCreateDTO;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports de Swagger 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "Reseñas", description = "Operaciones de gestión de reseñas y calificaciones")
@RestController
@RequestMapping("/api/v1/resenas")
public class ResenaController {

    @Autowired
    private ResenaService service;

    // --- GET /api/v1/resenas ---
    @Operation(summary = "Listar todas las reseñas", description = "Retorna la lista completa de reseñas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ResenaDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // --- GET /api/v1/resenas/{id} ---
    @Operation(summary = "Buscar reseña por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> getById(
        @Parameter(description = "ID único de la reseña", required = true) @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // --- POST /api/v1/resenas ---
    @Operation(summary = "Registrar nueva reseña")
    @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente")
    @PostMapping
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody ResenaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    // --- PUT /api/v1/resenas/{id} ---
    @Operation(summary = "Actualizar reseña existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> actualizar(
        @Parameter(description = "ID de la reseña a actualizar", required = true) @PathVariable Long id, 
        @Valid @RequestBody ResenaCreateDTO dto
    ) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // --- DELETE /api/v1/resenas/{id} ---
    @Operation(summary = "Eliminar reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseña eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró la reseña con el ID proporcionado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID de la reseña a eliminar", required = true) @PathVariable Long id
    ) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la reseña con ID: " + id));
    }
}