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

    // 1. Ver qué tiene el cliente en su carrito
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoItem>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    // 2. Agregar un producto al carrito
    @PostMapping
    public ResponseEntity<CarritoItem> agregar(@RequestBody CarritoItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarItem(item));
    }

    // 3. Vaciar el carrito (Esto lo usará Pedidos después de una compra exitosa)
    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciar(@PathVariable Long clienteId) {
        service.limpiarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }
}