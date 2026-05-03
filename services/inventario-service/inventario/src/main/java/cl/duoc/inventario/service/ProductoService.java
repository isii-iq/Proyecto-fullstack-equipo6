package cl.duoc.inventario.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import cl.duoc.inventario.model.Producto;
import cl.duoc.inventario.repository.ProductoRepository;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto){
        if(productoRepository.existsBySkuIgnoreCase(producto.getSku())){
            throw new RuntimeException("El SKU ya existe en el inventario");
        }
        return productoRepository.save(producto);
    }

  
    public Optional<Producto> actualizarProducto(Long id, Producto productoDetalles) {
        return productoRepository.findById(id).map(productoExistente -> {
            productoExistente.setNombre(productoDetalles.getNombre());
            productoExistente.setCategoria(productoDetalles.getCategoria());
            productoExistente.setCantidad(productoDetalles.getCantidad());
            productoExistente.setPrecio(productoDetalles.getPrecio());
            return productoRepository.save(productoExistente);
        });
    }

   //eliminar por id
    public boolean eliminarPorId(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

   //eliminar x sku
    public boolean eliminarPorSku(String sku){
        if (productoRepository.existsBySkuIgnoreCase(sku)){
            productoRepository.deleteBySkuIgnoreCase(sku);
            return true;
        }
        return false;
    }
}