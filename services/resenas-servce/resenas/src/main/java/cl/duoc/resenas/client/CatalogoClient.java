package cl.duoc.resenas.client;
import cl.duoc.resenas.dto.CatalogoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalogo-client", url = "${catalogo.service.url}")
public interface CatalogoClient {

    @GetMapping("/api/v1/productos/{id}") 
    CatalogoDTO obtenerProductoPorId(@PathVariable("id") Long id);
}