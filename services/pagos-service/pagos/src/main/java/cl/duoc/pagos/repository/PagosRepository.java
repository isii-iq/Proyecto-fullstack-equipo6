package cl.duoc.pagos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import cl.duoc.pagos.model.Pago;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Repository
public interface PagosRepository extends JpaRepository<Pago, Long> {

    //buscar por numero de boleta
    Optional<Pago> findByNumeroBoletaIgnoreCase(String numeroBoleta);


    boolean existsByNumeroBoletaIgnoreCase(String numeroBoleta);

    //buscar los pagos 
    List<Pago> findByMetodoPagoIgnoreCase(String metodoPago);

    @Modifying
    @Transactional
    void deleteByNumeroBoletaIgnoreCase(String numeroBoleta);
}