package cl.duoc.envios.service;

import cl.duoc.envios.client.ClienteClient;
import cl.duoc.envios.client.PedidoClient;
import cl.duoc.envios.dto.ClienteDTO;
import cl.duoc.envios.dto.PedidoDTO;
import cl.duoc.envios.model.Envio;
import cl.duoc.envios.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository repository;

    @Autowired
    private PedidoClient pedidoClient;

    @Autowired
    private ClienteClient clienteClient;

    public List<Envio> listarTodos() {
        return repository.findAll();
    }

    public Envio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Envio crearEnvioDesdePedido(Long pedidoId) {
    PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(pedidoId);

    ClienteDTO cliente = clienteClient.obtenerClientePorId(pedido.getClienteId());

    Envio envio = new Envio();
    envio.setPedidoId(pedidoId);
    
    envio.setDireccion(cliente.getDireccion());
    envio.setComuna(cliente.getComuna());
    
    envio.setEstadoEnvio("EN_PREPARACION");
    envio.setNumeroSeguimiento("SEG-" + System.currentTimeMillis());
    envio.setFechaDespacho(LocalDateTime.now());

    return repository.save(envio);
}

    public Envio actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = buscarPorId(id);
        envio.setEstadoEnvio(nuevoEstado);
        return repository.save(envio);
    }

    public Envio guardarManual(Envio envio) {
    return repository.save(envio);
}
}