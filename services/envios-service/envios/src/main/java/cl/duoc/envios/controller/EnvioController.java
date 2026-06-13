package cl.duoc.envios.controller;

import cl.duoc.envios.model.Envio;
import cl.duoc.envios.service.EnvioService;
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

@Tag(name = "Envíos", description = "Operaciones de gestión y despacho de envíos")
@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService service;

    // ── GET /api/v1/envios ────────────────────────────
    @Operation(summary = "Listar todos los envíos", 
               description = "Retorna la lista completa de despachos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // ── GET /api/v1/envios/{id} ───────────────────────
    @Operation(summary = "Buscar envío por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envío encontrado"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(
            @Parameter(description = "ID único del envío", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ── POST /api/v1/envios/generar/{pedidoId} ────────
    @Operation(summary = "Generar despacho automático desde un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Envío generado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido de origen no encontrado")
    })
    @PostMapping("/generar/{pedidoId}")
    public ResponseEntity<Envio> generarDespacho(
            @Parameter(description = "ID del pedido para procesar el envío", required = true)
            @PathVariable Long pedidoId) {
        return new ResponseEntity<>(service.crearEnvioDesdePedido(pedidoId), HttpStatus.CREATED);
    }

    // ── POST /api/v1/envios ───────────────────────────
    @Operation(summary = "Registrar nuevo envío manual")
    @ApiResponse(responseCode = "201", description = "Envío manual creado exitosamente")
    @PostMapping
    public ResponseEntity<Envio> crearManual(@Valid @RequestBody Envio envio) {
        return new ResponseEntity<>(service.guardarManual(envio), HttpStatus.CREATED);
    }

    // ── PUT /api/v1/envios/{id} ───────────────────────
    @Operation(summary = "Actualizar datos completos de un envío")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizarCompleto(
            @Parameter(description = "ID del envío a modificar", required = true)
            @PathVariable Long id, 
            @Valid @RequestBody Envio envioDatos) {
        return ResponseEntity.ok(service.actualizarDesdeJson(id, envioDatos));
    }

    // ── DELETE /api/v1/envios/{id} ────────────────────
    @Operation(summary = "Eliminar un envío")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminación exitosa (Sin contenido)"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del envío a eliminar", required = true)
            @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}