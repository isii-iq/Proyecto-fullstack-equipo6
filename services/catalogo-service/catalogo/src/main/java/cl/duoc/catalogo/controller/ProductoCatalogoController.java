package cl.duoc.catalogo.controller;

import cl.duoc.catalogo.dto.CatalogoCreateDTO;
import cl.duoc.catalogo.dto.CatalogoDTO;
import cl.duoc.catalogo.service.ProductoCatalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoCatalogoController {

    @Autowired
    private ProductoCatalogoService service;

    @GetMapping
    public ResponseEntity<List<CatalogoDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CatalogoDTO> crear(@Valid @RequestBody CatalogoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarProducto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CatalogoCreateDTO dto) {
        return ResponseEntity.ok(service.actualizarProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró el producto con ID: " + id));
    }

    @DeleteMapping("/sku/{sku}")
    public ResponseEntity<?> eliminarPorSKU(@PathVariable String sku) {
        boolean eliminado = service.eliminarPorSKU(sku);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto con SKU " + sku + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "SKU no encontrado"));
    }
}