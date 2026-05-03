package cl.duoc.catalogo.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import cl.duoc.catalogo.model.Producto;
import cl.duoc.catalogo.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyección por constructor (Recomendación IE 1.2.1)
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> obtenerDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        // Podríamos validar aquí si el SKU ya existe antes de guardar
        return productoRepository.save(producto);
    }

    // --- OPERACIÓN UPDATE (IE 1.2.3) ---
    public Optional<Producto> actualizarProducto(Long id, Producto detalles) {
        return productoRepository.findById(id).map(existente -> {
            existente.setNombre(detalles.getNombre());
            existente.setDescripcion(detalles.getDescripcion());
            existente.setPrecio(detalles.getPrecio());
            existente.setCategoria(detalles.getCategoria());
            existente.setDisponible(detalles.isDisponible());
            // El SKU no se debería cambiar una vez creado, por eso no lo incluimos
            return productoRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }
}