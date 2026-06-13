package cl.duoc.notificaciones.controller;

import cl.duoc.notificaciones.dto.NotificacionCreateDTO;
import cl.duoc.notificaciones.dto.NotificacionDTO;
import cl.duoc.notificaciones.service.NotificacionService;

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

@Tag(name = "Notificaciones", description = "Operaciones de gestión y envío de notificaciones del sistema")
@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    // ── GET /api/v1/notificaciones ─────────────────────────────────────────────
    @Operation(summary = "Listar todas las notificaciones",
               description = "Retorna la lista completa de alertas, correos e historiales registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // ── GET /api/v1/notificaciones/{id} ────────────────────────────────────────
    @Operation(summary = "Buscar notificación por ID",
               description = "Obtiene los detalles estructurados de una notificación específica.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación localizada correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró ningún registro con el ID ingresado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> getById(
            @Parameter(description = "ID único de la notificación", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // ── POST /api/v1/notificaciones ────────────────────────────────────────────
    @Operation(summary = "Crear notificación manual",
               description = "Registra una nueva alerta base en el sistema a partir de los datos del cliente.")
    @ApiResponse(responseCode = "201", description = "Notificación persistida y creada exitosamente")
    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(@Valid @RequestBody NotificacionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarNotificacion(dto));
    }

    // ── POST /api/v1/notificaciones/enviar-pedido/{pedidoId} ───────────────────
    @Operation(summary = "Generar notificación automatizada por Pedido",
               description = "Invocación encargada de procesar el estado de un pedido y despachar la alerta correspondiente.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Notificación vinculada al pedido despachada con éxito"),
        @ApiResponse(responseCode = "400", description = "ID de pedido inválido o inconsistente")
    })
    @PostMapping("/enviar-pedido/{pedidoId}")
    public ResponseEntity<NotificacionDTO> enviarPorPedido(
            @Parameter(description = "ID del pedido que gatilla el flujo de envío", required = true)
            @PathVariable Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generarNotificacionPedido(pedidoId));
    }

    // ── PUT /api/v1/notificaciones/{id} ────────────────────────────────────────
    @Operation(summary = "Actualizar notificación existente",
               description = "Reemplaza los campos informativos de una notificación mediante la carga de un payload JSON.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización realizada con éxito"),
        @ApiResponse(responseCode = "404", description = "La notificación solicitada no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotificacionDTO> actualizarCompleto(
            @Parameter(description = "ID de la notificación a modificar", required = true)
            @PathVariable Long id, 
            @Valid @RequestBody NotificacionCreateDTO dto) {
        return ResponseEntity.ok(service.actualizarDesdeJson(id, dto));
    }

    // ── DELETE /api/v1/notificaciones/{id} ─────────────────────────────────────
    @Operation(summary = "Eliminar notificación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación removida correctamente de los registros"),
        @ApiResponse(responseCode = "404", description = "No se encontró el recurso solicitado para remover")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @Parameter(description = "ID de la notificación a eliminar", required = true)
            @PathVariable Long id) {
        boolean eliminado = service.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la notificación con ID: " + id));
    }

    // ── DELETE /api/v1/notificaciones/usuario/{usuarioId} ──────────────────────
    @Operation(summary = "Eliminar todo el historial de notificaciones de un Usuario",
               description = "Limpia de forma masiva los registros vinculados a un identificador de usuario único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial de alertas del usuario purgado con éxito"),
        @ApiResponse(responseCode = "404", description = "El usuario solicitado no registra alertas activas")
    })
    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> eliminarPorUsuario(
            @Parameter(description = "ID del usuario dueño de las notificaciones", required = true)
            @PathVariable Long usuarioId) {
        boolean eliminado = service.eliminarPorUsuarioId(usuarioId);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Notificaciones del usuario " + usuarioId + " eliminadas"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontraron notificaciones para el usuario solicitado"));
    }
}