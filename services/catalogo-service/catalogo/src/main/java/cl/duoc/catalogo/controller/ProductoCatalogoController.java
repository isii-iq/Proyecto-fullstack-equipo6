package cl.duoc.catalogo.controller;

import cl.duoc.catalogo.dto.CatalogoCreateDTO;
import cl.duoc.catalogo.dto.CatalogoDTO;
import cl.duoc.catalogo.service.ProductoCatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Productos", description = "Operaciones de gestión de catálogo de productos")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoCatalogoController {

    @Autowired
    private ProductoCatalogoService service;

    @Operation(summary = "Listar todos los productos", description = "Retorna la lista completa de productos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CatalogoDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Buscar producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CatalogoDTO> getById(
        @Parameter(description = "ID único del producto", required = true) 
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Registrar nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @PostMapping
    public ResponseEntity<CatalogoDTO> crear(@Valid @RequestBody CatalogoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarProducto(dto));
    }

    @Operation(summary = "Actualizar producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CatalogoDTO> actualizar(
        @Parameter(description = "ID a actualizar") 
        @PathVariable Long id, 
        @Valid @RequestBody CatalogoCreateDTO dto
    ) {
        return ResponseEntity.ok(service.actualizarProducto(id, dto));
    }

    @Operation(summary = "Eliminar producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminación exitosa"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID a eliminar") 
        @PathVariable Long id
    ) {
        boolean eliminado = service.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró el producto con ID: " + id));
    }

    @Operation(summary = "Eliminar producto por SKU")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminación por SKU exitosa"),
        @ApiResponse(responseCode = "404", description = "SKU no encontrado")
    })
    @DeleteMapping("/sku/{sku}")
    public ResponseEntity<?> eliminarPorSKU(
        @Parameter(description = "Código SKU del producto", required = true) 
        @PathVariable String sku
    ) {
        boolean eliminado = service.eliminarPorSKU(sku);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto con SKU " + sku + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "SKU no encontrado"));
    }
}