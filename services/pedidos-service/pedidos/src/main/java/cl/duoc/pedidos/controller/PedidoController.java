package cl.duoc.pedidos.controller;

import cl.duoc.pedidos.dto.PedidoCreateDTO;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.service.PedidoService;
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

@Tag(name = "Pedidos", description = "Operaciones de gestión y seguimiento de pedidos")
@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    // --- GET /api/v1/pedidos ---
    @Operation(summary = "Listar todos los pedidos", description = "Retorna la lista completa de pedidos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Pedido>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // --- GET /api/v1/pedidos/{id} ---
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getById(
        @Parameter(description = "ID único del pedido", required = true) @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // --- POST /api/v1/pedidos ---
    @Operation(summary = "Registrar nuevo pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos del pedido inválidos o error en la solicitud")
    })
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PedidoCreateDTO dto) {
        try {
            Pedido nuevoPedido = service.guardar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- PUT /api/v1/pedidos/{id}/estado ---
    @Operation(summary = "Actualizar el estado de un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
        @Parameter(description = "ID del pedido a modificar", required = true) @PathVariable Long id, 
        @Parameter(description = "Nuevo estado del pedido (ej: PROCESANDO, ENVIADO, ENTREGADO)", required = true) @RequestParam String estado
    ) {
        try {
            Pedido pedidoActualizado = service.actualizarEstado(id, estado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- DELETE /api/v1/pedidos/{id} ---
    @Operation(summary = "Eliminar pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del pedido a eliminar", required = true) @PathVariable Long id
    ) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}