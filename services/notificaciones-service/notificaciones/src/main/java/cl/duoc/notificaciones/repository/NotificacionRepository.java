package cl.duoc.notificaciones.repository;

import cl.duoc.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    
    List<Notificacion> findByUsuarioId(Long usuarioId);
    long countByUsuarioIdAndLeidoFalse(Long usuarioId);
    List<Notificacion> findByTituloContainingIgnoreCase(String titulo);
}