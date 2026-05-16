package cl.duoc.notificaciones.controller;

import cl.duoc.notificaciones.dto.NotificacionCreateDTO;
import cl.duoc.notificaciones.dto.NotificacionDTO;
import cl.duoc.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(@Valid @RequestBody NotificacionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarNotificacion(dto));
    }

    @PostMapping("/enviar-pedido/{pedidoId}")
    public ResponseEntity<NotificacionDTO> enviarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generarNotificacionPedido(pedidoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la notificación con ID: " + id));
    }

    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> eliminarPorUsuario(@PathVariable Long usuarioId) {
        boolean eliminado = service.eliminarPorUsuarioId(usuarioId);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Notificaciones del usuario " + usuarioId + " eliminadas"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontraron notificaciones para el usuario solicitado"));
    }
}