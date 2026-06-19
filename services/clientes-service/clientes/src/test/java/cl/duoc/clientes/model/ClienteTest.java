package cl.duoc.clientes.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        Cliente cliente = new Cliente();
        assertNotNull(cliente);
    }

    @Test
    @DisplayName("Constructor completo - debe asignar todos los campos correctamente")
    void constructorCompletoDebeAsignarTodosLosCampos() {
        LocalDateTime fecha = LocalDateTime.now();
        
        Cliente cliente = new Cliente(
                1L,
                "Juan",
                "Pérez",
                "12345678-9",
                "juan.perez@mail.cl",
                "+56912345678",
                "Av. Siempre Viva 742",
                "Santiago",
                true,
                fecha
        );

        assertEquals(1L, cliente.getId());
        assertEquals("Juan", cliente.getNombre());
        assertEquals("Pérez", cliente.getApellidoPaterno());
        assertEquals("12345678-9", cliente.getRut());
        assertEquals("juan.perez@mail.cl", cliente.getCorreo());
        assertEquals("+56912345678", cliente.getTelefono());
        assertEquals("Av. Siempre Viva 742", cliente.getDireccion());
        assertEquals("Santiago", cliente.getComuna());
        assertTrue(cliente.isActivo());
        assertEquals(fecha, cliente.getFechaRegistro());
    }

    @Test
    @DisplayName("Setters - debe permitir modificar cada campo individualmente")
    void settersDebenPermitirModificarCampos() {
        Cliente cliente = new Cliente();

        cliente.setId(2L);
        cliente.setNombre("María");
        cliente.setApellidoPaterno("González");
        cliente.setRut("9876543-2");
        cliente.setCorreo("maria.g@mail.cl");
        cliente.setTelefono("+56987654321");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setComuna("Providencia");
        cliente.setActivo(false);
        LocalDateTime fecha = LocalDateTime.now();
        cliente.setFechaRegistro(fecha);

        assertEquals(2L, cliente.getId());
        assertEquals("María", cliente.getNombre());
        assertEquals("González", cliente.getApellidoPaterno());
        assertEquals("9876543-2", cliente.getRut());
        assertEquals("maria.g@mail.cl", cliente.getCorreo());
        assertEquals("+56987654321", cliente.getTelefono());
        assertEquals("Calle Falsa 123", cliente.getDireccion());
        assertEquals("Providencia", cliente.getComuna());
        assertFalse(cliente.isActivo());
        assertEquals(fecha, cliente.getFechaRegistro());
    }

    @Test
    @DisplayName("equals y hashCode - dos clientes con los mismos datos deben ser iguales")
    void dosClientesConMismosDatosDebenSerIguales() {
        LocalDateTime fecha = LocalDateTime.now();
        
        Cliente c1 = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan.perez@mail.cl", "+56912345678", "Av. Siempre Viva 742", "Santiago", true, fecha);
        Cliente c2 = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan.perez@mail.cl", "+56912345678", "Av. Siempre Viva 742", "Santiago", true, fecha);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el nombre del cliente en la representación")
    void toStringDebeContenerNombreDelCliente() {
        LocalDateTime fecha = LocalDateTime.now();
        Cliente cliente = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan.perez@mail.cl", "+56912345678", "Av. Siempre Viva 742", "Santiago", true, fecha);

        String texto = cliente.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("Juan"));
    }

    @Test
    @DisplayName("PrePersist - debe asignar la fecha de registro automáticamente")
    void testPrePersist_OnCreate() {
        Cliente cliente = new Cliente();
        
        assertNull(cliente.getFechaRegistro());
        
        cliente.onCreate();
        
        assertNotNull(cliente.getFechaRegistro());
        assertTrue(cliente.getFechaRegistro().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}