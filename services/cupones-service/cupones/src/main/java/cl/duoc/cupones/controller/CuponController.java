package cl.duoc.cupones.controller;

import cl.duoc.cupones.model.Cupon;
import cl.duoc.cupones.service.CuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Cupones", description = "Operaciones de gestión y aplicación de cupones de descuento")
@RestController
@RequestMapping("/api/v1/cupones")
public class CuponController {

    @Autowired
    private CuponService service;

    // ── GET /api/v1/cupones ───────────────────────────
    @Operation(summary = "Listar todos los cupones",
               description = "Retorna la lista completa de cupones registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Cupon>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // ── GET /api/v1/cupones/{id} ──────────────────────
    @Operation(summary = "Buscar cupón por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cupón encontrado"),
        @ApiResponse(responseCode = "404", description = "Cupón no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cupon> obtenerPorId(
            @Parameter(description = "ID único del cupón", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ── POST /api/v1/cupones ──────────────────────────
    @Operation(summary = "Registrar nuevo cupón")
    @ApiResponse(responseCode = "201", description = "Cupón creado exitosamente")
    @PostMapping
    public ResponseEntity<Cupon> crear(@Valid @RequestBody Cupon cupon) {
        return new ResponseEntity<>(service.crear(cupon), HttpStatus.CREATED);
    }

    // ── PUT /api/v1/cupones/{id} ──────────────────────
    @Operation(summary = "Actualizar cupón existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Cupón no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Cupon> actualizar(
            @Parameter(description = "ID del cupón a actualizar")
            @PathVariable Long id, 
            @Valid @RequestBody Cupon cupon) {
        return ResponseEntity.ok(service.actualizar(id, cupon));
    }

    // ── DELETE /api/v1/cupones/{id} ───────────────────
    @Operation(summary = "Eliminar cupón")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminación exitosa (Sin contenido)"),
        @ApiResponse(responseCode = "404", description = "Cupón no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cupón a eliminar")
            @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ── POST /api/v1/cupones/{id}/aplicar ─────────────
    @Operation(summary = "Aplicar cupón a un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cupón aplicado exitosamente al pedido"),
        @ApiResponse(responseCode = "404", description = "Cupón o pedido no encontrado")
    })
    @PostMapping("/{id}/aplicar")
    public ResponseEntity<Map<String, Object>> aplicarCupon(
            @Parameter(description = "ID del cupón a aplicar", required = true)
            @PathVariable Long id) {
        Double nuevoTotal = service.aplicarDescuentoAPedido(id);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Cupón aplicado exitosamente",
            "nuevoTotalPedido", nuevoTotal
        ));
    }
}