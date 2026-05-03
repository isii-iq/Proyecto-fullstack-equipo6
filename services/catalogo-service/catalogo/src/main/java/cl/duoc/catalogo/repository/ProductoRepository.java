package cl.duoc.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.catalogo.model.Producto;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar por SKU 
    Optional<Producto> findBySkuIgnoreCase(String sku);

   
    boolean existsBySkuIgnoreCase(String sku);

    // Buscar productos por categoría 
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    // Buscar disponibles
    List<Producto> findByDisponibleTrue();
}