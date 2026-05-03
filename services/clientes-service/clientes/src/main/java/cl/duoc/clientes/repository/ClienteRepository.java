package cl.duoc.clientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Importante para borrados
import org.springframework.stereotype.Repository;
import cl.duoc.clientes.model.Cliente;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    boolean existsByRutIgnoreCase(String rut);
    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Cliente> findByRutIgnoreCase(String rut);

    @Modifying
    @Transactional
    void deleteByRutIgnoreCase(String rut);
}