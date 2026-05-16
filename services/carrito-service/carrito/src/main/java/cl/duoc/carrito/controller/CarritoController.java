package cl.duoc.carrito.controller;

import cl.duoc.carrito.model.CarritoItem;
import cl.duoc.carrito.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    @Autowired
    private CarritoService service;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoItem>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<CarritoItem> agregar(@RequestBody CarritoItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarItem(item));
    }

    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciar(@PathVariable Long clienteId) {
        service.limpiarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoItem> actualizarCantidad(@PathVariable Long id, @RequestBody Integer nuevaCantidad) {
      CarritoItem actualizado = service.actualizarCantidad(id, nuevaCantidad);
      return ResponseEntity.ok(actualizado);
    }
}