package cl.duoc.resenas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.resenas.model.Resena;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProductoId(Long productoId);

    List<Resena> findByClienteId(Long clienteId);

   
}