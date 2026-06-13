package cl.duoc.inventario.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        Producto producto = new Producto();
        assertNotNull(producto);
    }

    @Test
    @DisplayName("Constructor completo - debe asignar todos los campos correctamente")
    void constructorCompletoDebeAsignarTodosLosCampos() {
        Producto producto = new Producto(
            1L, 
            "Laptop Lenovo", 
            "Computación", 
            10, 
            "LNV-12345-MX", 
            new BigDecimal("649990.00")
        );

        assertEquals(1L, producto.getId());
        assertEquals("Laptop Lenovo", producto.getNombre());
        assertEquals("Computación", producto.getCategoria());
        assertEquals(10, producto.getCantidad());
        assertEquals("LNV-12345-MX", producto.getSku());
        assertEquals(new BigDecimal("649990.00"), producto.getPrecio());
    }

    @Test
    @DisplayName("Setters - debe permitir modificar cada campo individualmente")
    void settersDebenPermitirModificarCampos() {
        Producto producto = new Producto();

        producto.setId(2L);
        producto.setNombre("Mouse Logitech MX Master 3");
        producto.setCategoria("Accesorios");
        producto.setCantidad(25);
        producto.setSku("LOG-98765-MX");
        producto.setPrecio(new BigDecimal("89990.00"));

        assertEquals(2L, producto.getId());
        assertEquals("Mouse Logitech MX Master 3", producto.getNombre());
        assertEquals("Accesorios", producto.getCategoria());
        assertEquals(25, producto.getCantidad());
        assertEquals("LOG-98765-MX", producto.getSku());
        assertEquals(new BigDecimal("89990.00"), producto.getPrecio());
    }

    @Test
    @DisplayName("equals y hashCode - dos productos con los mismos datos deben ser iguales")
    void dosProductosConMismosDatosDebenSerIguales() {
        Producto p1 = new Producto(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"));
        Producto p2 = new Producto(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"));

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el nombre del producto en la representación")
    void toStringDebeContenerNombreDelProducto() {
        Producto producto = new Producto(3L, "Teclado Mecánico Redragon", "Accesorios", 15, "RED-44332-RGB", new BigDecimal("59990.00"));

        String texto = producto.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("Teclado Mecánico Redragon"));
        assertTrue(texto.contains("RED-44332-RGB")); 
    }
}