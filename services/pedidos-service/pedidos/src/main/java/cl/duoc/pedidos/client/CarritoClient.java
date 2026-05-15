package cl.duoc.pedidos.client;

import cl.duoc.pedidos.dto.CarritoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "carrito-service", url = "${carrito.service.url}")
public interface CarritoClient {

    @GetMapping("/api/v1/carrito/cliente/{clienteId}")
    List<CarritoDTO> getCarritoByCliente(@PathVariable("clienteId") Long clienteId);

    @DeleteMapping("/api/v1/carrito/cliente/{clienteId}")
    void limpiarCarrito(@PathVariable("clienteId") Long clienteId);
}