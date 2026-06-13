package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.InventarioCreateDTO;
import cl.duoc.inventario.dto.InventarioDTO;
import cl.duoc.inventario.service.ProductoService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Operaciones de gestión de productos en el inventario")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @Operation(summary = "Listar todos los productos", description = "Retorna la lista completa de productos registrados en el inventario.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Buscar producto por ID", description = "Retorna un único producto según su identificador numérico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> getById(
            @Parameter(description = "ID único del producto", required = true) 
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Registrar nuevo producto", description = "Crea un nuevo producto en el sistema de inventario.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarProducto(dto));
    }

    @Operation(summary = "Actualizar producto existente", description = "Modifica los datos de un producto ya registrado mediante su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true) 
            @PathVariable Long id, 
            @Valid @RequestBody InventarioCreateDTO dto) {
        return ResponseEntity.ok(service.actualizarProducto(id, dto));
    }

    @Operation(summary = "Eliminar producto por ID", description = "Elimina físicamente o lógicamente un producto usando su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto con el ID especificado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true) 
            @PathVariable Long id) {
        boolean eliminado = service.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró el producto con ID: " + id));
    }

    @Operation(summary = "Eliminar producto por SKU", description = "Elimina un producto del inventario utilizando su código SKU.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto con SKU eliminado"),
        @ApiResponse(responseCode = "404", description = "SKU no encontrado")
    })
    @DeleteMapping("/sku/{sku}")
    public ResponseEntity<?> eliminarPorSKU(
            @Parameter(description = "Código SKU del producto", required = true) 
            @PathVariable String sku) {
        boolean eliminado = service.eliminarPorSKU(sku);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto con SKU " + sku + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "SKU no encontrado"));
    }

    @Operation(summary = "Validar stock disponible", description = "Verifica si un producto cuenta con la cantidad solicitada en stock.")
    @ApiResponse(responseCode = "200", description = "Validación ejecutada (retorna true o false)")
    @GetMapping("/validar/{id}/{cantidad}")
    public ResponseEntity<Boolean> validarStock(
            @Parameter(description = "ID del producto a validar", required = true) @PathVariable Long id, 
            @Parameter(description = "Cantidad requerida para verificar", required = true) @PathVariable int cantidad) {
        InventarioDTO prod = service.obtenerPorId(id);
        boolean tieneStock = prod != null && prod.getCantidad() >= cantidad;
        return ResponseEntity.ok(tieneStock);
    }

    @Operation(summary = "Buscar producto por SKU", description = "Retorna la información de un producto basándose en su código SKU único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "SKU no encontrado")
    })
    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventarioDTO> getBySku(
            @Parameter(description = "Código SKU del producto a buscar", required = true) 
            @PathVariable String sku) {
        return ResponseEntity.ok(service.obtenerPorSku(sku));
    }
}