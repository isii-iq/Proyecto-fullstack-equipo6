package cl.duoc.catalogo.client;

import cl.duoc.catalogo.dto.InventarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventario-service", url = "${inventario.service.url}")
public interface InventarioClient {
    
    @GetMapping("/api/v1/productos/sku/{sku}")
    InventarioDTO obtenerStockPorSku(@PathVariable("sku") String sku);
}