package cl.duoc.resenas.client;

import cl.duoc.resenas.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes-client", url = "${clientes.service.url}")
public interface ClienteClient {

    @GetMapping("/api/v1/clientes/{id}")
    ClienteDTO getClienteById(@PathVariable("id") Long id);
}