package cl.duoc.carrito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.carrito.model.CarritoItem;

public interface CarritoRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByClienteId(Long clienteId);
    void deleteByClienteId(Long clienteId);
}