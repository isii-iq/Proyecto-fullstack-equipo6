package cl.duoc.envios.controller;

import cl.duoc.envios.model.Envio;
import cl.duoc.envios.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService service;

    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping("/generar/{pedidoId}")
    public ResponseEntity<Envio> generarDespacho(@PathVariable Long pedidoId) {
        return new ResponseEntity<>(service.crearEnvioDesdePedido(pedidoId), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Envio> crearManual(@Valid @RequestBody Envio envio) {
        return new ResponseEntity<>(service.guardarManual(envio), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizarCompleto(@PathVariable Long id, @Valid @RequestBody Envio envioDatos) {
        return ResponseEntity.ok(service.actualizarDesdeJson(id, envioDatos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}