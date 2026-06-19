package cl.duoc.pedidos.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrdenItemTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        OrdenItem item = new OrdenItem();
        assertNotNull(item);
    }

    @Test
    @DisplayName("Setters y Getters - debe permitir asignar y recuperar cada campo individualmente")
    void settersYGettersDebenFuncionarCorrectamente() {
        OrdenItem item = new OrdenItem();
        Pedido pedido = new Pedido();
        pedido.setId(1L);

        item.setId(10L);
        item.setProductoId(55L);
        item.setCantidad(3);
        item.setPrecioUnitario(4990.0);
        item.setPedido(pedido);

        assertEquals(10L, item.getId());
        assertEquals(55L, item.getProductoId());
        assertEquals(3, item.getCantidad());
        assertEquals(4990.0, item.getPrecioUnitario());
        assertEquals(pedido, item.getPedido());
    }

    @Test
    @DisplayName("equals y hashCode - dos items con los mismos datos deben ser iguales")
    void dosItemsConMismosDatosDebenSerIguales() {
        OrdenItem item1 = new OrdenItem();
        item1.setId(1L);
        item1.setProductoId(55L);
        item1.setCantidad(2);
        item1.setPrecioUnitario(1500.0);

        OrdenItem item2 = new OrdenItem();
        item2.setId(1L);
        item2.setProductoId(55L);
        item2.setCantidad(2);
        item2.setPrecioUnitario(1500.0);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el ID del producto en la representación")
    void toStringDebeContenerIdDelProducto() {
        OrdenItem item = new OrdenItem();
        item.setId(1L);
        item.setProductoId(777L);

        String texto = item.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("777"));
    }
}