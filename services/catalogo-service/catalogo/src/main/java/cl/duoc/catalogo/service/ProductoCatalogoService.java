package cl.duoc.catalogo.service;

import cl.duoc.catalogo.client.InventarioClient;
import cl.duoc.catalogo.dto.CatalogoCreateDTO;
import cl.duoc.catalogo.dto.CatalogoDTO;
import cl.duoc.catalogo.dto.InventarioDTO;
import cl.duoc.catalogo.excepciones.RecursoNoEncontradoException;
import cl.duoc.catalogo.model.ProductoCatalogo;
import cl.duoc.catalogo.repository.ProductoCatalogoRepository;
import feign.FeignException;
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

    // --- SE MANTIENE EL MÉTODO QUE CONSULTA POR ID ---
    public CatalogoDTO obtenerPorId(Long id) {
        log.info("Buscando producto id={} en catálogo local", id);
        
        ProductoCatalogo producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado en Catálogo: " + id));

        // Validación dinámica de existencia/stock en ms-inventario
        validarSkuEnInventario(producto.getSku());

        return toDTO(producto);
    }

    // --- VALIDACIÓN INTEGRADA AL CREAR PRODUCTO ---
    public CatalogoDTO guardarProducto(CatalogoCreateDTO dto) {
        log.info("Solicitud para crear nuevo producto con SKU: {}", dto.getSku());
        
        // 1. Forzamos la validación remota antes de persistir el registro
        validarSkuEnInventario(dto.getSku());
        
        // 2. Si es exitosa, se procede a guardar localmente
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

        // (Opcional) Si en la actualización modifican el SKU, puedes agregar aquí también el validador
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

    // --- MÉTODO PRIVADO CENTRALIZADO DE VALIDACIÓN FEIGN ---
    private void validarSkuEnInventario(String sku) {
        try {
            log.info("Validando existencia y stock en ms-inventario para SKU: {}", sku);
            InventarioDTO stockInfo = inventarioClient.obtenerStockPorSku(sku);
            
            if (stockInfo == null) {
                throw new RecursoNoEncontradoException("El servicio de inventario no retornó información para el SKU: " + sku);
            }
            
            if (stockInfo.getCantidad() <= 0) {
                log.warn("El producto con SKU {} existe pero no tiene stock disponible", sku);
                throw new RuntimeException("El producto no cuenta con stock disponible en bodega.");
            }
            
            log.info("Validación exitosa en inventario para SKU: {}. Stock: {}", sku, stockInfo.getCantidad());
            
        } catch (FeignException.NotFound e) {
            log.error("Validación fallida: El SKU {} no existe en el sistema de Inventario", sku);
            throw new RecursoNoEncontradoException("No se puede operar: El SKU " + sku + " no existe registrado en Inventario.");
        } catch (FeignException e) {
            log.error("Error crítico de comunicación con ms-inventario al validar SKU: {}", sku);
            throw new RuntimeException("Servicio de Inventario no disponible. Intente más tarde.");
        }
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