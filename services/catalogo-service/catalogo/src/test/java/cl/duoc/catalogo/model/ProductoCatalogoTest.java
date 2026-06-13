package cl.duoc.catalogo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ProductoCatalogoTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        ProductoCatalogo producto = new ProductoCatalogo();
        assertNotNull(producto);
    }

    @Test
    @DisplayName("Constructor completo - debe asignar todos los campos correctamente")
    void constructorCompletoDebeAsignarTodosLosCampos() {
        LocalDateTime fecha = LocalDateTime.now();
        
        ProductoCatalogo producto = new ProductoCatalogo(
                1L,
                "Teclado Mecánico",
                "Switch Red RGB",
                45000.0,
                "Periféricos",
                "TEC-12345",
                true,
                fecha
        );

        assertEquals(1L, producto.getId());
        assertEquals("Teclado Mecánico", producto.getNombre());
        assertEquals("Switch Red RGB", producto.getDescripcion());
        assertEquals(45000.0, producto.getPrecio());
        assertEquals("Periféricos", producto.getCategoria());
        assertEquals("TEC-12345", producto.getSku());
        assertTrue(producto.isDisponible());
        assertEquals(fecha, producto.getFechaCreacion());
    }

    @Test
    @DisplayName("Setters - debe permitir modificar cada campo individualmente")
    void settersDebenPermitirModificarCampos() {
        ProductoCatalogo producto = new ProductoCatalogo();

        producto.setId(2L);
        producto.setNombre("Mouse Óptico");
        producto.setDescripcion("Mouse Gamer 16000 DPI");
        producto.setPrecio(25000.0);
        producto.setCategoria("Periféricos");
        producto.setSku("MOU-98765");
        producto.setDisponible(false);
        LocalDateTime fecha = LocalDateTime.now();
        producto.setFechaCreacion(fecha);

        assertEquals(2L, producto.getId());
        assertEquals("Mouse Óptico", producto.getNombre());
        assertEquals("Mouse Gamer 16000 DPI", producto.getDescripcion());
        assertEquals(25000.0, producto.getPrecio());
        assertEquals("Periféricos", producto.getCategoria());
        assertEquals("MOU-98765", producto.getSku());
        assertFalse(producto.isDisponible());
        assertEquals(fecha, producto.getFechaCreacion());
    }

    @Test
    @DisplayName("equals y hashCode - dos productos con los mismos datos deben ser iguales")
    void dosProductosConMismosDatosDebenSerIguales() {
        LocalDateTime fecha = LocalDateTime.now();
        
        ProductoCatalogo p1 = new ProductoCatalogo(1L, "Teclado Mecánico", "Switch Red RGB", 45000.0, "Periféricos", "TEC-12345", true, fecha);
        ProductoCatalogo p2 = new ProductoCatalogo(1L, "Teclado Mecánico", "Switch Red RGB", 45000.0, "Periféricos", "TEC-12345", true, fecha);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el nombre del producto en la representación")
    void toStringDebeContenerNombreDelProducto() {
        LocalDateTime fecha = LocalDateTime.now();
        ProductoCatalogo producto = new ProductoCatalogo(1L, "Teclado Mecánico", "Switch Red RGB", 45000.0, "Periféricos", "TEC-12345", true, fecha);

        String texto = producto.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("Teclado Mecánico"));
    }

    @Test
    @DisplayName("PrePersist - debe asignar la fecha de creación automáticamente")
    void testPrePersist_OnCreate() {
        ProductoCatalogo producto = new ProductoCatalogo();
        
        assertNull(producto.getFechaCreacion());
        
        producto.onCreate();
        
        assertNotNull(producto.getFechaCreacion());
        assertTrue(producto.getFechaCreacion().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}