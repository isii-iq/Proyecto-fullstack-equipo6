package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.ClienteClient;
import cl.duoc.pedidos.client.ProductoClient;
import cl.duoc.pedidos.dto.PedidoCreateDTO;
import cl.duoc.pedidos.dto.ProductoCatalogoDTO;
import cl.duoc.pedidos.model.OrdenItem;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired 
    private PedidoRepository repository;
    
    @Autowired 
    private ClienteClient clienteClient;
    
    @Autowired 
    private ProductoClient productoClient;

    public List<Pedido> obtenerTodos() {
        return repository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    @Transactional
    public Pedido guardar(PedidoCreateDTO dto) {
        try {
            clienteClient.getClienteById(dto.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("Error: El cliente con ID " + dto.getClienteId() + " no existe.");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());
        pedido.setEstado("PENDIENTE");

        List<OrdenItem> listaItems = new ArrayList<>();
        double totalCalculado = 0.0;

        for (var itemDto : dto.getItems()) {
            ProductoCatalogoDTO producto = productoClient.getById(itemDto.getProductoId());
            
            if (producto == null) {
                throw new RuntimeException("El producto ID " + itemDto.getProductoId() + " no existe en el catálogo.");
            }

            OrdenItem item = new OrdenItem();
            item.setProductoId(itemDto.getProductoId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            item.setPedido(pedido);

            listaItems.add(item);
            totalCalculado += (producto.getPrecio() * itemDto.getCantidad());
        }

        pedido.setItems(listaItems);
        pedido.setTotal(totalCalculado);

        return repository.save(pedido);
    }

    @Transactional
    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        pedido.setEstado(nuevoEstado);
        return repository.save(pedido);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}