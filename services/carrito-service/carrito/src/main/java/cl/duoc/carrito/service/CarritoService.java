package cl.duoc.carrito.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.duoc.carrito.model.CarritoItem;
import cl.duoc.carrito.repository.CarritoRepository;

@Service
public class CarritoService {
    @Autowired
    private CarritoRepository repository;

    public List<CarritoItem> obtenerPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public CarritoItem agregarItem(CarritoItem item) {
        return repository.save(item);
    }
    
    public void limpiarCarrito(Long clienteId) {
        repository.deleteByClienteId(clienteId);
    }

    public CarritoItem actualizarCantidad(Long id, Integer nuevaCantidad) {
        CarritoItem item = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));
    
        item.setCantidad(nuevaCantidad);
        return repository.save(item);
    }
}