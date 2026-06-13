package cl.duoc.pagos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Imports de Swagger (Agregados según la guía)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.duoc.pagos.dto.PagoCreateDTO;
import cl.duoc.pagos.dto.PagoDTO;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.service.PagoService;
import jakarta.validation.Valid;

@Tag(name = "Pagos", description = "Operaciones de gestión de pagos")
@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    @Autowired
    private PagoService service;

    // --- GET /api/v1/pagos ---
    @Operation(summary = "Listar todos los pagos", description = "Retorna la lista completa de pagos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PagoDTO>> listarTodos() {
        List<PagoDTO> lista = service.listarTodos().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // --- GET /api/v1/pagos/{id} ---
    @Operation(summary = "Buscar pago por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPorId(
        @Parameter(description = "ID único del pago", required = true) @PathVariable Long id
    ) {
        return ResponseEntity.ok(convertirADTO(service.buscarPorId(id)));
    }

    // --- POST /api/v1/pagos ---
    @Operation(summary = "Registrar nuevo pago")
    @ApiResponse(responseCode = "201", description = "Pago creado exitosamente")
    @PostMapping
    public ResponseEntity<PagoDTO> realizarPago(@Valid @RequestBody PagoCreateDTO dto) {
        Pago pago = service.procesarPago(dto.getPedidoId(), dto.getMetodoPago());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(pago));
    }

    // --- PUT /api/v1/pagos/{id} ---
    @Operation(summary = "Actualizar pago existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizar(
        @Parameter(description = "ID del pago a actualizar", required = true) @PathVariable Long id, 
        @RequestBody Pago pagoActualizado
    ) {
        return ResponseEntity.ok(convertirADTO(service.actualizar(id, pagoActualizado)));
    }

    // --- DELETE /api/v1/pagos/{id} ---
    @Operation(summary = "Eliminar pago")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminación exitosa"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID del pago a eliminar", required = true) @PathVariable Long id
    ) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private PagoDTO convertirADTO(Pago pago) {
        return new PagoDTO(
            pago.getId(),
            pago.getPedidoId(),
            pago.getMonto(),
            pago.getMetodoPago(),
            pago.getEstado(),
            pago.getFechaTransaccion()
        );
    }
}