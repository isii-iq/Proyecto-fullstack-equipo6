package cl.duoc.pedidos.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        Pedido pedido = new Pedido();
        assertNotNull(pedido);
    }

    @Test
    @DisplayName("Constructor completo - debe asignar todos los campos correctamente")
    void constructorCompletoDebeAsignarTodosLosCampos() {
        List<OrdenItem> items = new ArrayList<>();
        LocalDateTime fecha = LocalDateTime.now();

        Pedido pedido = new Pedido(
            1L, 100L, 45000.0, "PROCESANDO", fecha, items
        );

        assertEquals(1L, pedido.getId());
        assertEquals(100L, pedido.getClienteId());
        assertEquals(45000.0, pedido.getTotal());
        assertEquals("PROCESANDO", pedido.getEstado());
        assertEquals(fecha, pedido.getFechaPedido());
        assertEquals(items, pedido.getItems());
    }

    @Test
    @DisplayName("Setters - debe permitir modificar cada campo individualmente")
    void settersDebenPermitirModificarCampos() {
        Pedido pedido = new Pedido();
        List<OrdenItem> items = new ArrayList<>();
        LocalDateTime fecha = LocalDateTime.now();

        pedido.setId(2L);
        pedido.setClienteId(200L);
        pedido.setTotal(15990.0);
        pedido.setEstado("ENVIADO");
        pedido.setFechaPedido(fecha);
        pedido.setItems(items);

        assertEquals(2L, pedido.getId());
        assertEquals(200L, pedido.getClienteId());
        assertEquals(15990.0, pedido.getTotal());
        assertEquals("ENVIADO", pedido.getEstado());
        assertEquals(fecha, pedido.getFechaPedido());
        assertEquals(items, pedido.getItems());
    }

    @Test
    @DisplayName("PrePersist onCreate - debe asignar fecha actual y estado PENDIENTE por defecto")
    void prePersistDebeAsignarValoresPorDefecto() {
        Pedido pedido = new Pedido();
        pedido.onCreate();

        assertNotNull(pedido.getFechaPedido());
        assertEquals("PENDIENTE", pedido.getEstado());
    }

    @Test
    @DisplayName("PrePersist onCreate - no debe sobreescribir el estado si ya está asignado")
    void prePersistNoDebeSobreescribirEstadoExistente() {
        Pedido pedido = new Pedido();
        pedido.setEstado("ENTREGADO");
        
        pedido.onCreate();

        assertEquals("ENTREGADO", pedido.getEstado());
    }

    @Test
    @DisplayName("equals y hashCode - dos pedidos con los mismos datos deben ser iguales")
    void dosPedidosConMismosDatosDebenSerIguales() {
        List<OrdenItem> items = new ArrayList<>();
        LocalDateTime fecha = LocalDateTime.now();

        Pedido p1 = new Pedido(1L, 100L, 45000.0, "PENDIENTE", fecha, items);
        Pedido p2 = new Pedido(1L, 100L, 45000.0, "PENDIENTE", fecha, items);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el estado del pedido en la representación")
    void toStringDebeContenerEstadoDelPedido() {
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setEstado("COMPLETADO");

        String texto = pedido.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("COMPLETADO"));
    }
}
