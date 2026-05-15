package cl.duoc.envios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.envios.dto.PedidoDTO;

@FeignClient(name = "pedido-client", url = "${pedido.service.url}")
public interface PedidoClient {
    @GetMapping("/api/v1/pedidos/{id}")
    PedidoDTO obtenerPedidoPorId(@PathVariable("id") Long id);
}