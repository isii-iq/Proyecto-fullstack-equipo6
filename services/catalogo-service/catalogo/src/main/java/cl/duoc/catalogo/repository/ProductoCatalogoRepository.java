package cl.duoc.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.catalogo.model.ProductoCatalogo;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoCatalogoRepository extends JpaRepository<ProductoCatalogo, Long> {

    // Buscar por SKU 
    Optional<ProductoCatalogo> findBySkuIgnoreCase(String sku);

   
    boolean existsBySkuIgnoreCase(String sku);

    // Buscar productos por categoría 
    List<ProductoCatalogo> findByCategoriaIgnoreCase(String categoria);

    // Buscar disponibles
    List<ProductoCatalogo> findByDisponibleTrue();
}