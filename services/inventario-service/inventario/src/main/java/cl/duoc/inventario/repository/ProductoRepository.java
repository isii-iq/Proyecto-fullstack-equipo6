package cl.duoc.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import cl.duoc.inventario.model.Producto;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {


    boolean existsBySkuIgnoreCase(String sku);

    
    Optional<Producto> findBySkuIgnoreCase(String sku);

    @Modifying
    @Transactional
    void deleteBySkuIgnoreCase(String sku);
}

