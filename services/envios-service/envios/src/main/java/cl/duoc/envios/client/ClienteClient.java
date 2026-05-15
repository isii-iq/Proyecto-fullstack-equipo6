package cl.duoc.envios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.envios.dto.ClienteDTO;

@FeignClient(name = "cliente-client", url = "${cliente.service.url}")
public interface ClienteClient {
    @GetMapping("/api/v1/clientes/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}