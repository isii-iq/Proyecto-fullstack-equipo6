package cl.duoc.pedidos.client;

import cl.duoc.pedidos.dto.ProductoCatalogoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-catalogo", url = "${catalogo.service.url}")
public interface ProductoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoCatalogoDTO getById(@PathVariable("id") Long id);
}