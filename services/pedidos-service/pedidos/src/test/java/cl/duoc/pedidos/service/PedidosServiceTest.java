package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.ClienteClient;
import cl.duoc.pedidos.client.ProductoClient;
import cl.duoc.pedidos.dto.PedidoCreateDTO;
import cl.duoc.pedidos.dto.ClienteDTO;
import cl.duoc.pedidos.dto.ItemCreateDTO;
import cl.duoc.pedidos.dto.ProductoCatalogoDTO;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("obtenerTodos - debe retornar lista de pedidos cuando existen registros")
    void debeRetornarListaDePedidos() {
        List<Pedido> pedidosSimulados = List.of(
            new Pedido(1L, 101L, 25000.0, "PENDIENTE", null, new ArrayList<>()),
            new Pedido(2L, 102L, 45000.0, "PROCESANDO", null, new ArrayList<>())
        );
        when(repository.findAll()).thenReturn(pedidosSimulados);

        List<Pedido> resultado = pedidoService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(101L, resultado.get(0).getClienteId());
        assertEquals(25000.0, resultado.get(0).getTotal());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodos - debe retornar lista vacía cuando no hay pedidos")
    void debeRetornarListaVaciaSiNoHayPedidos() {
        when(repository.findAll()).thenReturn(List.of());

        List<Pedido> resultado = pedidoService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerPorId - debe retornar el pedido correcto cuando existe")
    void debeRetornarPedidoPorId() {
        Pedido pedido = new Pedido(1L, 101L, 15000.0, "PENDIENTE", null, new ArrayList<>());
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(101L, resultado.getClienteId());
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar RuntimeException cuando el ID no existe")
    void debeLanzarExcepcionCuandoPedidoNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () ->
            pedidoService.obtenerPorId(999L)
        );
        assertEquals("Pedido no encontrado con ID: 999", excepcion.getMessage());
    }

    @Test
    @DisplayName("guardar - debe crear el pedido calculando el total correctamente cuando cliente e items existen")
    void debeGuardarPedidoCorrectamente() {
        ItemCreateDTO itemDto = new ItemCreateDTO(); 
        itemDto.setProductoId(55L);
        itemDto.setCantidad(2);

        PedidoCreateDTO dto = new PedidoCreateDTO();
        dto.setClienteId(101L);
        dto.setItems(List.of(itemDto));

        ProductoCatalogoDTO productoCatalogo = new ProductoCatalogoDTO();
        productoCatalogo.setId(55L);
        productoCatalogo.setPrecio(10000.0);

        when(clienteClient.getClienteById(101L)).thenReturn((ClienteDTO) new Object()); 
        when(productoClient.getById(55L)).thenReturn(productoCatalogo);
        
        when(repository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(7L); 
            return p;
        });

        Pedido resultado = pedidoService.guardar(dto);

        assertNotNull(resultado);
        assertEquals(7L, resultado.getId());
        assertEquals(101L, resultado.getClienteId());
        assertEquals(20000.0, resultado.getTotal());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(1, resultado.getItems().size());
        verify(repository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("guardar - debe lanzar excepción si el cliente no existe")
    void debeLanzarExcepcionSiClienteNoExiste() {
        PedidoCreateDTO dto = new PedidoCreateDTO();
        dto.setClienteId(404L);
        
        when(clienteClient.getClienteById(404L)).thenThrow(new RuntimeException("Not Found"));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () ->
            pedidoService.guardar(dto)
        );
        assertEquals("Error: El cliente con ID 404 no existe.", excepcion.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("guardar - debe lanzar excepción si alguno de los productos no existe")
    void debeLanzarExcepcionSiProductoNoExiste() {
        ItemCreateDTO itemDto = new ItemCreateDTO();
        itemDto.setProductoId(999L);
        itemDto.setCantidad(1);

        PedidoCreateDTO dto = new PedidoCreateDTO();
        dto.setClienteId(101L);
        dto.setItems(List.of(itemDto));

        when(clienteClient.getClienteById(101L)).thenReturn((ClienteDTO) new Object());
        when(productoClient.getById(999L)).thenReturn(null);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () ->
            pedidoService.guardar(dto)
        );
        assertEquals("El producto ID 999 no existe en el catálogo.", excepcion.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("actualizarEstado - debe modificar el estado de un pedido existente")
    void debeActualizarEstadoCorrectamente() {
        Pedido pedidoOriginal = new Pedido(1L, 101L, 30000.0, "PENDIENTE", null, new ArrayList<>());
        when(repository.findById(1L)).thenReturn(Optional.of(pedidoOriginal));
        when(repository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.actualizarEstado(1L, "ENVIADO");

        assertNotNull(resultado);
        assertEquals("ENVIADO", resultado.getEstado());
        verify(repository, times(1)).save(pedidoOriginal);
    }

    @Test
    @DisplayName("eliminar - debe retornar false si el ID del pedido no existe")
    void debeRetornarFalseAlEliminarInexistente() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = pedidoService.eliminar(999L);

        assertFalse(resultado);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar - debe retornar true e invocar deleteById si el pedido existe")
    void debeEliminarPedidoExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = pedidoService.eliminar(1L);

        assertTrue(resultado);
        verify(repository, times(1)).deleteById(1L);
    }
}