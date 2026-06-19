package cl.duoc.resenas.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ResenaTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        Resena resena = new Resena();
        assertNotNull(resena);
    }

    @Test
    @DisplayName("Constructor completo - debe asignar todos los campos correctamente")
    void constructorCompletoDebeAsignarTodosLosCampos() {
        LocalDateTime fecha = LocalDateTime.now();
        
        Resena resena = new Resena(
                1L,
                100L,
                500L,
                "Excelente producto, muy recomendado.",
                5,
                fecha
        );

        assertEquals(1L, resena.getId());
        assertEquals(100L, resena.getProductoId());
        assertEquals(500L, resena.getClienteId());
        assertEquals("Excelente producto, muy recomendado.", resena.getComentario());
        assertEquals(5, resena.getCalificacion());
        assertEquals(fecha, resena.getFechaCreacion());
    }

    @Test
    @DisplayName("Setters - debe permitir modificar cada campo individualmente")
    void settersDebenPermitirModificarCampos() {
        Resena resena = new Resena();

        resena.setId(2L);
        resena.setProductoId(101L);
        resena.setClienteId(501L);
        resena.setComentario("El envío tardó un poco, pero el artículo está bien.");
        resena.setCalificacion(4);
        LocalDateTime fecha = LocalDateTime.now();
        resena.setFechaCreacion(fecha);

        assertEquals(2L, resena.getId());
        assertEquals(101L, resena.getProductoId());
        assertEquals(501L, resena.getClienteId());
        assertEquals("El envío tardó un poco, pero el artículo está bien.", resena.getComentario());
        assertEquals(4, resena.getCalificacion());
        assertEquals(fecha, resena.getFechaCreacion());
    }

    @Test
    @DisplayName("equals y hashCode - dos reseñas con los mismos datos deben ser iguales")
    void dosResenasConMismosDatosDebenSerIguales() {
        LocalDateTime fecha = LocalDateTime.now();
        
        Resena r1 = new Resena(1L, 100L, 500L, "Excelente producto, muy recomendado.", 5, fecha);
        Resena r2 = new Resena(1L, 100L, 500L, "Excelente producto, muy recomendado.", 5, fecha);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el comentario de la reseña en la representación")
    void toStringDebeContenerComentarioDeLaResena() {
        LocalDateTime fecha = LocalDateTime.now();
        Resena resena = new Resena(1L, 100L, 500L, "Excelente producto, muy recomendado.", 5, fecha);

        String texto = resena.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("Excelente producto, muy recomendado."));
    }

    @Test
    @DisplayName("PrePersist - debe asignar la fecha de creación automáticamente")
    void testPrePersist_OnCreate() {
        Resena resena = new Resena();
        
        assertNull(resena.getFechaCreacion());
        
        resena.prePersist();
        
        assertNotNull(resena.getFechaCreacion());
        assertTrue(resena.getFechaCreacion().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}