package cl.duoc.catalogo.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.catalogo.model.Producto;
import cl.duoc.catalogo.service.ProductoService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> listarDisponibles() {
        return ResponseEntity.ok(productoService.obtenerDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
    // Mantengo el HttpStatus.CREATED (201) que es el estándar correcto para POST
    return new ResponseEntity<>(productoService.guardar(producto), HttpStatus.CREATED);
}

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto)
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }   
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (productoService.eliminar(id)) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se pudo eliminar: Producto no encontrado"));
    }
}