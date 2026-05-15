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

import cl.duoc.pagos.dto.PagoCreateDTO;
import cl.duoc.pagos.dto.PagoDTO;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.service.PagoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    @Autowired
    private PagoService service;

    @GetMapping
    public ResponseEntity<List<PagoDTO>> listarTodos() {
        List<PagoDTO> lista = service.listarTodos().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(convertirADTO(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<PagoDTO> realizarPago(@Valid @RequestBody PagoCreateDTO dto) {
        Pago pago = service.procesarPago(dto.getPedidoId(), dto.getMetodoPago());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(pago));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizar(@PathVariable Long id, @RequestBody Pago pagoActualizado) {
        return ResponseEntity.ok(convertirADTO(service.actualizar(id, pagoActualizado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
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