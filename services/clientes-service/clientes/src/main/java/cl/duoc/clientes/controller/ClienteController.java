package cl.duoc.clientes.controller;

import java.util.List;
import java.util.Map; // Para enviar respuestas JSON estructuradas

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/clientes") // IE 1.1.1: Recurso en plural y versionado
public class ClienteController {
    
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return new ResponseEntity<>(clienteService.obtenerTodos(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteService.guardarCliente(cliente), HttpStatus.CREATED);
    }

    // --- MEJORA IE 1.2.3: OPERACIÓN UPDATE (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return clienteService.actualizarCliente(id, cliente)
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // --- MEJORA IE 1.1.2: ELIMINAR POR ID Y RESPUESTA JSON ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        boolean eliminado = clienteService.eliminarPorId(id);
        if (eliminado) {
            // IE 1.1.3: Devolvemos un JSON estructurado en lugar de un String plano
            return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado con éxito"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("error", "No se encontró el cliente con ID: " + id));
        }
    }

    // Mantenemos el de RUT como un endpoint adicional si es necesario
    @DeleteMapping("/rut/{rut}")
    public ResponseEntity<?> eliminarPorRut(@PathVariable String rut) {
        if (clienteService.eliminarPorRut(rut)) {
            return ResponseEntity.ok(Map.of("mensaje", "Cliente con RUT " + rut + " eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(Map.of("error", "RUT no encontrado"));
    }
}