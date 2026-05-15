package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.InventarioCreateDTO;
import cl.duoc.inventario.dto.InventarioDTO;
import cl.duoc.inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarProducto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody InventarioCreateDTO dto) {
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

    @GetMapping("/validar/{id}/{cantidad}")
    public ResponseEntity<Boolean> validarStock(@PathVariable Long id, @PathVariable int cantidad) {
        InventarioDTO prod = service.obtenerPorId(id);
        // Asumiendo que InventarioDTO tiene un campo 'cantidad' o 'stock'
        boolean tieneStock = prod != null && prod.getCantidad() >= cantidad;
        return ResponseEntity.ok(tieneStock);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventarioDTO> getBySku(@PathVariable String sku) {
        // Asegúrate de que tu service tenga este método 'obtenerPorSku'
        return ResponseEntity.ok(service.obtenerPorSku(sku));
    }

}