package cl.duoc.envios.repository;

import cl.duoc.envios.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByNumeroSeguimientoIgnoreCase(String numeroSeguimiento);
    boolean existsByNumeroSeguimientoIgnoreCase(String numeroSeguimiento);
}