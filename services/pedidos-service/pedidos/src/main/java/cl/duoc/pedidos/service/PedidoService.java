package cl.duoc.pedidos.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> actualizar(Long id, Pedido pedidoDetalles) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setClienteId(pedidoDetalles.getClienteId());
            pedido.setTotal(pedidoDetalles.getTotal());
            pedido.setEstado(pedidoDetalles.getEstado());
            // Al actualizar la lista, JPA se encarga del resto por el CascadeType.ALL
            pedido.getItems().clear();
            pedido.getItems().addAll(pedidoDetalles.getItems());
            return pedidoRepository.save(pedido);
        });
    }

    public boolean eliminar(Long id) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedidoRepository.delete(pedido);
            return true;
        }).orElse(false);
    }
}