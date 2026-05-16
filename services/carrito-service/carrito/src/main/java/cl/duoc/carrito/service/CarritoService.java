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
        // Podrías agregar lógica para que si el producto ya existe, sume la cantidad
        return repository.save(item);
    }
    
    public void limpiarCarrito(Long clienteId) {
        repository.deleteByClienteId(clienteId);
    }
}