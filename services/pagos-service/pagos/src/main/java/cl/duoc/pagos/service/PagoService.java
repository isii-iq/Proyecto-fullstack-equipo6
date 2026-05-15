package cl.duoc.pagos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.pagos.client.PedidoClient;
import cl.duoc.pagos.dto.PedidoDTO;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagoRepository;
import feign.FeignException;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    @Autowired
    private PagoRepository repository;

    @Autowired
    private PedidoClient pedidoClient;

    public Pago procesarPago(Long pedidoId, String metodoPago) {
        log.info("Iniciando procesamiento de pago para el Pedido ID: {}", pedidoId);

        PedidoDTO pedido;
        try {
            log.info("Consultando datos del pedido en ms-pedidos...");
            pedido = pedidoClient.obtenerPedidoPorId(pedidoId);
        } catch (FeignException.NotFound e) {
            log.error("Error: El pedido {} no existe", pedidoId);
            throw new RuntimeException("No se puede pagar: El pedido no existe.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-pedidos: {}", e.getMessage());
            throw new RuntimeException("Error de conexión con el servicio de Pedidos.");
        }

        Pago pago = new Pago();
        pago.setPedidoId(pedido.getId());
        pago.setMonto(pedido.getTotal()); 
        pago.setMetodoPago(metodoPago);
        pago.setEstado("APROBADO");
        pago.setFechaTransaccion(LocalDateTime.now());

        Pago guardado = repository.save(pago);
        log.info("Pago ID: {} registrado exitosamente para el Pedido: {}", guardado.getId(), pedidoId);

        return guardado;
    }

    public List<Pago> listarTodos() {
    return repository.findAll();
}

public Pago buscarPorId(Long id) {
    return repository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
}

public void eliminar(Long id) {
    repository.deleteById(id);
}

public Pago actualizar(Long id, Pago nuevo) {
    Pago existente = buscarPorId(id);
    existente.setMetodoPago(nuevo.getMetodoPago());
    existente.setEstado(nuevo.getEstado());
    return repository.save(existente);
}
}