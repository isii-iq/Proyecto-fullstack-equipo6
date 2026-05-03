package cl.duoc.pagos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.service.PagoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listarPagos() {
        return new ResponseEntity<>(pagoService.obtenerTodos(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pago> crearPago(@Valid @RequestBody Pago pago) {
        return new ResponseEntity<>(pagoService.registrarPago(pago), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        return pagoService.actualizarEstado(id, nuevoEstado)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/boleta/{numeroBoleta}")
    public ResponseEntity<?> eliminarPorBoleta(@PathVariable String numeroBoleta) {
        if (pagoService.eliminarPorBoleta(numeroBoleta)) {
            return ResponseEntity.ok(Map.of("mensaje", "Pago con boleta " + numeroBoleta + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(Map.of("error", "Número de boleta no encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPago(@PathVariable Long id, @Valid @RequestBody Pago pago) {
        return pagoService.actualizarPago(id, pago)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}