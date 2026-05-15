package cl.duoc.catalogo.service;

import cl.duoc.catalogo.client.InventarioClient;
import cl.duoc.catalogo.dto.CatalogoCreateDTO;
import cl.duoc.catalogo.dto.CatalogoDTO;
import cl.duoc.catalogo.dto.InventarioDTO; // Asegúrate de tener este DTO creado
import cl.duoc.catalogo.excepciones.RecursoNoEncontradoException;
import cl.duoc.catalogo.model.ProductoCatalogo;
import cl.duoc.catalogo.repository.ProductoCatalogoRepository;
import feign.FeignException; // Importante para capturar errores de red
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoCatalogoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoCatalogoService.class);

    @Autowired
    private ProductoCatalogoRepository repository;

    @Autowired
    private InventarioClient inventarioClient;

    public List<CatalogoDTO> obtenerTodos() {
        log.info("Consultando todos los productos del catálogo");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- ESTE MÉTODO ES EL QUE USA MS-PEDIDOS ---
    public CatalogoDTO obtenerPorId(Long id) {
        log.info("Buscando producto id={} y validando stock en Inventario", id);
        
        // 1. Buscamos en nuestra base de datos local
        ProductoCatalogo producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado en Catálogo: " + id));

        // 2. Validación de Stock con MS-INVENTARIO (Salto de Microservicio)
        try {
            log.info("Llamando a ms-inventario para SKU: {}", producto.getSku());
            InventarioDTO stockInfo = inventarioClient.obtenerStockPorSku(producto.getSku());
            
            if (stockInfo == null || stockInfo.getCantidad() <= 0) {
                log.warn("El producto con SKU {} no tiene stock disponible", producto.getSku());
                throw new RuntimeException("Producto sin stock disponible en bodega.");
            }
            
            log.info("Stock validado exitosamente: {} unidades disponibles", stockInfo.getCantidad());
            
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El producto con SKU " + producto.getSku() + " no existe en Inventario.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-inventario");
            throw new RuntimeException("Servicio de Inventario no disponible actualmente.");
        }

        return toDTO(producto);
    }

    public CatalogoDTO guardarProducto(CatalogoCreateDTO dto) {
        log.info("Creando nuevo producto con SKU: {}", dto.getSku());
        
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setDisponible(dto.isDisponible());
        producto.setCategoria(dto.getCategoria());

        ProductoCatalogo guardado = repository.save(producto);
        log.info("Producto creado exitosamente con ID: {} y SKU: {}", guardado.getId(), guardado.getSku());
        
        return toDTO(guardado);
    }

    public CatalogoDTO actualizarProducto(Long id, CatalogoCreateDTO dto) {
        log.info("Actualizando producto id={}", id);

        ProductoCatalogo existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto para actualizar: " + id));

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());
        existente.setDisponible(dto.isDisponible());
        existente.setCategoria(dto.getCategoria());

        ProductoCatalogo actualizado = repository.save(existente);
        log.info("Producto id={} actualizado correctamente", actualizado.getId());
        
        return toDTO(actualizado);
    }

    public boolean eliminarPorId(Long id) {
        log.info("Intentando eliminar producto id={}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Producto id={} eliminado", id);
            return true;
        }
        log.warn("No se pudo eliminar: producto id={} no existe", id);
        return false;
    }

    public boolean eliminarPorSKU(String sku) {
        log.info("Intentando eliminar producto con SKU={}", sku);
        if (repository.existsBySkuIgnoreCase(sku)) {
            ProductoCatalogo producto = repository.findBySkuIgnoreCase(sku)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado para eliminar: " + sku));
            repository.delete(producto);
            log.info("Producto con SKU={} eliminado", sku);
            return true;
        }
        log.warn("No se pudo eliminar: SKU={} no encontrado", sku);
        return false;
    }

    private CatalogoDTO toDTO(ProductoCatalogo p) {
        return new CatalogoDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getCategoria(),
                p.getSku(),
                p.isDisponible()  
        );
    }
}