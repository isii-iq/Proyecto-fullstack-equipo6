package cl.duoc.cupones.controller;

import cl.duoc.cupones.model.Cupon;
import cl.duoc.cupones.service.CuponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cupones")
public class CuponController {

    @Autowired
    private CuponService service;

    @GetMapping
    public ResponseEntity<List<Cupon>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cupon> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Cupon> crear(@Valid @RequestBody Cupon cupon) {
        return new ResponseEntity<>(service.crear(cupon), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cupon> actualizar(@PathVariable Long id, @Valid @RequestBody Cupon cupon) {
        return ResponseEntity.ok(service.actualizar(id, cupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/aplicar")
    public ResponseEntity<Map<String, Object>> aplicarCupon(@PathVariable Long id) {
        Double nuevoTotal = service.aplicarDescuentoAPedido(id);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Cupón aplicado exitosamente",
            "nuevoTotalPedido", nuevoTotal
        ));
    }
}