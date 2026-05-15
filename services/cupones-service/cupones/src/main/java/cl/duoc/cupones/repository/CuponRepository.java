package cl.duoc.cupones.repository;

import cl.duoc.cupones.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {
    
    Optional<Cupon> findByCodigo(String codigo);
    
    List<Cupon> findByPedidoId(Long pedidoId);
    
    boolean existsByCodigo(String codigo);
}