package cl.duoc.inventario.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.inventario.model.Producto;
import cl.duoc.inventario.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return new ResponseEntity<>(productoService.obtenerTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        return new ResponseEntity<>(productoService.guardarProducto(producto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productoService.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado con éxito"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("error", "No se encontró el producto con ID: " + id));
        }
    }

    //eliminar por SKU
    @DeleteMapping("/sku/{sku}")
    public ResponseEntity<?> eliminarPorSku(@PathVariable String sku) {
        if (productoService.eliminarPorSku(sku)) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto con SKU " + sku + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(Map.of("error", "SKU no encontrado"));
    }
}