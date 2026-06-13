package cl.duoc.carrito.controller;

import cl.duoc.carrito.model.CarritoItem;
import cl.duoc.carrito.service.CarritoService;
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

@Tag(name = "Carrito", description = "Operaciones de gestión del carrito de compras")
@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    @Autowired
    private CarritoService service;

    // --- GET /api/v1/carrito/cliente/{clienteId} ---
    @Operation(summary = "Obtener carrito por cliente", description = "Retorna la lista de ítems en el carrito de un cliente específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado o sin carrito activo")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoItem>> getByCliente(
        @Parameter(description = "ID único del cliente", required = true) @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    // --- POST /api/v1/carrito ---
    @Operation(summary = "Agregar ítem al carrito")
    @ApiResponse(responseCode = "201", description = "Ítem agregado exitosamente")
    @PostMapping
    public ResponseEntity<CarritoItem> agregar(@RequestBody CarritoItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarItem(item));
    }

    // --- DELETE /api/v1/carrito/cliente/{clienteId} ---
    @Operation(summary = "Vaciar carrito del cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciar(
        @Parameter(description = "ID del cliente para vaciar su carrito", required = true) @PathVariable Long clienteId
    ) {
        service.limpiarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }

    // --- PUT /api/v1/carrito/{id} ---
    @Operation(summary = "Actualizar cantidad de un ítem")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cantidad actualizada con éxito"),
        @ApiResponse(responseCode = "404", description = "Ítem de carrito no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarritoItem> actualizarCantidad(
        @Parameter(description = "ID del ítem en el carrito a modificar", required = true) @PathVariable Long id, 
        @RequestBody Integer nuevaCantidad
    ) {
        CarritoItem actualizado = service.actualizarCantidad(id, nuevaCantidad);
        return ResponseEntity.ok(actualizado);
    }
}