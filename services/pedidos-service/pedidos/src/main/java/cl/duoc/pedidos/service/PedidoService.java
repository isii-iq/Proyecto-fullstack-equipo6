package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.CarritoClient;
import cl.duoc.pedidos.client.ClienteClient;
import cl.duoc.pedidos.client.ProductoClient;
import cl.duoc.pedidos.dto.CarritoDTO;
import cl.duoc.pedidos.dto.ClienteDTO;
import cl.duoc.pedidos.dto.PedidoCreateDTO;
import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.excepciones.RecursoNoEncontradoException;
import cl.duoc.pedidos.excepciones.ServicioNoDisponibleException;
import cl.duoc.pedidos.model.OrdenItem;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ClienteClient clienteClient; 

    @Autowired
    private ProductoClient productoClient;

    @Autowired
    private CarritoClient carritoClient;

    public List<PedidoDTO> obtenerTodos() {
        log.info("Consultando todos los pedidos registrados"); 
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PedidoDTO obtenerPorId(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
        return toDTO(pedido);
    }

    public PedidoDTO guardar(PedidoCreateDTO dto) {
    log.info("Iniciando flujo de creación de pedido desde CARRITO para clienteId: {}", dto.getClienteId());

    // 1. VALIDACIÓN DE CLIENTE
    try {
        log.info("Validando cliente ID: {}", dto.getClienteId());
        clienteClient.getClienteById(dto.getClienteId());
    } catch (FeignException.NotFound e) {
        throw new RecursoNoEncontradoException("El cliente no existe.");
    } catch (FeignException e) {
        throw new ServicioNoDisponibleException("Microservicio de Clientes no disponible.");
    }

    // 2. OBTENER ÍTEMS DEL CARRITO (Nueva conexión)
    List<CarritoDTO> itemsDelCarrito;
    try {
        log.info("Recuperando productos del carrito para cliente: {}", dto.getClienteId());
        itemsDelCarrito = carritoClient.getCarritoByCliente(dto.getClienteId());
        
        if (itemsDelCarrito == null || itemsDelCarrito.isEmpty()) {
            throw new RuntimeException("No se puede crear el pedido: El carrito está vacío.");
        }
    } catch (FeignException e) {
        throw new ServicioNoDisponibleException("Microservicio de Carrito no disponible.");
    }

    // 3. PREPARACIÓN DEL OBJETO PEDIDO
    Pedido pedido = new Pedido();
    pedido.setClienteId(dto.getClienteId());
    pedido.setEstado("PENDIENTE");
    // El total ahora se calcula o se recibe del carrito si prefieres
    pedido.setTotal(dto.getTotal()); 

    // 4. VALIDACIÓN DE CADA PRODUCTO EN CATÁLOGO (Y por ende, Inventario)
    List<OrdenItem> listaItems = itemsDelCarrito.stream().map(itemCarrito -> {
        try {
            log.info("Validando disponibilidad en Catálogo para producto ID: {}", itemCarrito.getProductoId());
            // Catálogo revisará internamente el stock en Inventario
            productoClient.getById(itemCarrito.getProductoId()); 
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto ID " + itemCarrito.getProductoId() + " sin stock o no existe.");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("Error al validar disponibilidad en la cadena Catálogo-Inventario.");
        }

        OrdenItem item = new OrdenItem();
        item.setProductoId(itemCarrito.getProductoId());
        item.setCantidad(itemCarrito.getCantidad());
        // Aquí podrías llamar al catálogo para traer el precio actualizado si no viene en el carrito
        item.setPrecioUnitario(0.0); 
        return item;    
    }).collect(Collectors.toList());

    pedido.setItems(listaItems);

    // 5. GUARDAR PEDIDO
    Pedido guardado = repository.save(pedido);
    log.info("Pedido id={} generado con éxito", guardado.getId());

    // 6. LIMPIAR EL CARRITO (Post-venta)
    try {
        log.info("Limpiando carrito del cliente {} tras compra exitosa", dto.getClienteId());
        carritoClient.limpiarCarrito(dto.getClienteId());
    } catch (Exception e) {
        log.error("No se pudo limpiar el carrito, pero el pedido fue creado.");
    }

    return toDTO(guardado);
    }

    public boolean eliminar(Long id) {
        log.info("Solicitud de eliminación para pedido ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Pedido ID: {} eliminado correctamente", id);
            return true;
        }
        log.warn("Intento de eliminación fallido: Pedido ID: {} no existe", id);
        return false;
    }

    private PedidoDTO toDTO(Pedido p) {
        return new PedidoDTO(
                p.getId(),
                p.getClienteId(),
                p.getTotal(),
                p.getEstado(),
                p.getFechaPedido()
        );
    }
}