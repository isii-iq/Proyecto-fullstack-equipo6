package cl.duoc.clientes.controller;

import cl.duoc.clientes.dto.ClienteDTO;
import cl.duoc.clientes.dto.ClienteCreateDTO;
import cl.duoc.clientes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports de Swagger 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "Clientes", description = "Operaciones de gestión de clientes y perfiles de usuario")
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    // --- GET /api/v1/clientes ---
    @Operation(summary = "Listar todos los clientes", description = "Retorna la lista completa de clientes registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAll() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // --- GET /api/v1/clientes/{id} ---
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getById(
        @Parameter(description = "ID único del cliente", required = true) @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // --- POST /api/v1/clientes ---
    @Operation(summary = "Registrar nuevo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente")
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarCliente(dto));
    }

    // --- PUT /api/v1/clientes/{id} ---
    @Operation(summary = "Actualizar cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(
        @Parameter(description = "ID del cliente a actualizar", required = true) @PathVariable Long id, 
        @Valid @RequestBody ClienteCreateDTO dto
    ) {
        return ResponseEntity.ok(service.actualizarCliente(id, dto));
    }

    // --- DELETE /api/v1/clientes/{id} ---
    @Operation(summary = "Eliminar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró el cliente con el ID proporcionado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del cliente a eliminar", required = true) @PathVariable Long id
    ) {
        boolean eliminado = service.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado con éxito"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró el cliente con ID: " + id));
    }

    // --- DELETE /api/v1/clientes/rut/{rut} ---
    @Operation(summary = "Eliminar cliente por RUT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "RUT no encontrado en el sistema")
    })
    @DeleteMapping("/rut/{rut}")
    public ResponseEntity<?> eliminarPorRut(
        @Parameter(description = "RUT del cliente a eliminar (con guion y dígito verificador)", required = true) @PathVariable String rut
    ) {
        boolean eliminado = service.eliminarPorRut(rut);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Cliente con RUT " + rut + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "RUT no encontrado"));
    }
}