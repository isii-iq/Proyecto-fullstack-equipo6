package cl.duoc.notificaciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.notificaciones.dto.ClienteDTO;

@FeignClient(name = "cliente-client", url = "${clientes.service.url}")
public interface ClienteClient {

    @GetMapping("/api/v1/clientes/{id}")
    ClienteDTO getClienteById(@PathVariable("id") Long id);
    
}