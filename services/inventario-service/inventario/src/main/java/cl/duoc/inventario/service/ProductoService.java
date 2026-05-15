package cl.duoc.inventario.service;

import cl.duoc.inventario.dto.InventarioCreateDTO;
import cl.duoc.inventario.dto.InventarioDTO;
import cl.duoc.inventario.excepciones.RecursoNoEncontradoException;
import cl.duoc.inventario.model.Producto;
import cl.duoc.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository repository;

    public List<InventarioDTO> obtenerTodos() {
        log.info("Consultando todos los productos del inventario");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventarioDTO obtenerPorId(Long id) {
        log.info("Buscando producto id={}", id);
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
        return toDTO(producto);
    }

    public InventarioDTO obtenerPorSku(String sku) {
        log.info("Buscando producto por SKU: {}", sku);
        Producto producto = repository.findBySkuIgnoreCase(sku)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con SKU " + sku + " no encontrado en inventario"));
        return toDTO(producto);
    }

    public InventarioDTO guardarProducto(InventarioCreateDTO dto) {
        log.info("Creando nuevo producto con SKU: {}", dto.getSku());

        Producto producto = new Producto();
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setCantidad(dto.getCantidad());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(dto.getCategoria());

        Producto guardado = repository.save(producto);
        log.info("Producto creado exitosamente con ID: {} y SKU: {}", guardado.getId(), guardado.getSku());
        
        return toDTO(guardado);
    }

    public InventarioDTO actualizarProducto(Long id, InventarioCreateDTO dto) {
        log.info("Actualizando producto id={}", id);

        Producto existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto para actualizar: " + id));

        existente.setNombre(dto.getNombre());
        existente.setCantidad(dto.getCantidad());
        existente.setPrecio(dto.getPrecio());
        existente.setCategoria(dto.getCategoria());

        Producto actualizado = repository.save(existente);
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
            Producto producto = repository.findBySkuIgnoreCase(sku).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado para eliminar: " + sku));
            repository.delete(producto);
            log.info("Producto con SKU={} eliminado", sku);
            return true;
        }
        log.warn("No se pudo eliminar: SKU={} no encontrado", sku);
        return false;
    }

    private InventarioDTO toDTO(Producto p) {
        return new InventarioDTO(
                p.getId(),
                p.getNombre(),
                p.getCategoria(),
                p.getCantidad(),
                p.getSku(),
                p.getPrecio()
                
               
             
               
        );
    }
}




