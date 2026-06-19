package cl.duoc.clientes.repository;

import cl.duoc.clientes.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    @DisplayName("save - debe persistir el cliente y asignar un ID generado automáticamente")
    void debePersistirClienteYAsignarIdGenerado() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellidoPaterno("Pérez");
        cliente.setRut("12345678-9");
        cliente.setCorreo("juan.perez@mail.cl");
        cliente.setTelefono("+56912345678");
        cliente.setDireccion("Av. Siempre Viva 742");
        cliente.setComuna("Santiago");
        cliente.setActivo(true);

        Cliente guardado = repository.save(cliente);

        assertNotNull(guardado.getId());
        assertTrue(guardado.getId() > 0);
        assertEquals("Juan", guardado.getNombre());
        assertEquals("12345678-9", guardado.getRut());
    }

    @Test
    @DisplayName("findAll - debe retornar todos los clientes guardados en la BD")
    void debeRetornarTodosLosClientesGuardados() {
        Cliente c1 = new Cliente();
        c1.setNombre("Juan");
        c1.setApellidoPaterno("Pérez");
        c1.setRut("12345678-9");
        c1.setCorreo("juan@mail.cl");
        c1.setTelefono("+5691");
        c1.setDireccion("Direccion 1");
        c1.setComuna("Comuna 1");
        repository.save(c1);

        Cliente c2 = new Cliente();
        c2.setNombre("María");
        c2.setApellidoPaterno("González");
        c2.setRut("9876543-2");
        c2.setCorreo("maria@mail.cl");
        c2.setTelefono("+5692");
        c2.setDireccion("Direccion 2");
        c2.setComuna("Comuna 2");
        repository.save(c2);

        List<Cliente> clientes = repository.findAll();

        assertNotNull(clientes);
        assertEquals(2, clientes.size());
    }

    @Test
    @DisplayName("findById - debe retornar el cliente correcto cuando el ID existe")
    void debeEncontrarClientePorIdExistente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellidoPaterno("Pérez");
        cliente.setRut("12345678-9");
        cliente.setCorreo("juan@mail.cl");
        cliente.setTelefono("+5691");
        cliente.setDireccion("Direccion 1");
        cliente.setComuna("Comuna 1");
        Cliente guardado = repository.save(cliente);

        Optional<Cliente> resultado = repository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    @DisplayName("findById - debe retornar Optional vacío cuando el ID no existe")
    void debeRetornarOptionalVacioCuandoIdNoExiste() {
        Optional<Cliente> resultado = repository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("deleteById - debe eliminar el cliente de la base de datos")
    void debeEliminarClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellidoPaterno("Pérez");
        cliente.setRut("12345678-9");
        cliente.setCorreo("juan@mail.cl");
        cliente.setTelefono("+5691");
        cliente.setDireccion("Direccion 1");
        cliente.setComuna("Comuna 1");
        Cliente guardado = repository.save(cliente);
        Long id = guardado.getId();

        repository.deleteById(id);

        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    @DisplayName("existsByRutIgnoreCase - debe retornar verdadero si el RUT ya existe sin importar mayúsculas/minúsculas")
    void testExistsByRutIgnoreCase() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Diego");
        cliente.setApellidoPaterno("Muñoz");
        cliente.setRut("19876543-K");
        cliente.setCorreo("diego@mail.cl");
        cliente.setTelefono("+5693");
        cliente.setDireccion("Direccion 3");
        cliente.setComuna("Comuna 3");
        repository.save(cliente);

        boolean existeMinuscula = repository.existsByRutIgnoreCase("19876543-k");
        boolean existeMayuscula = repository.existsByRutIgnoreCase("19876543-K");
        boolean noExiste = repository.existsByRutIgnoreCase("11111111-1");

        assertTrue(existeMinuscula);
        assertTrue(existeMayuscula);
        assertFalse(noExiste);
    }

    @Test
    @DisplayName("existsByCorreoIgnoreCase - debe retornar verdadero si el correo ya existe sin importar mayúsculas/minúsculas")
    void testExistsByCorreoIgnoreCase() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Diego");
        cliente.setApellidoPaterno("Muñoz");
        cliente.setRut("19876543-K");
        cliente.setCorreo("Diego@Mail.cl");
        cliente.setTelefono("+5693");
        cliente.setDireccion("Direccion 3");
        cliente.setComuna("Comuna 3");
        repository.save(cliente);

        boolean existeMinuscula = repository.existsByCorreoIgnoreCase("diego@mail.cl");
        boolean existeMayuscula = repository.existsByCorreoIgnoreCase("DIEGO@MAIL.CL");
        boolean noExiste = repository.existsByCorreoIgnoreCase("falso@mail.cl");

        assertTrue(existeMinuscula);
        assertTrue(existeMayuscula);
        assertFalse(noExiste);
    }

    @Test
    @DisplayName("findByRutIgnoreCase - debe retornar el cliente correcto por RUT sin importar la K")
    void testFindByRutIgnoreCase() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Diego");
        cliente.setApellidoPaterno("Muñoz");
        cliente.setRut("19876543-K");
        cliente.setCorreo("diego@mail.cl");
        cliente.setTelefono("+5693");
        cliente.setDireccion("Direccion 3");
        cliente.setComuna("Comuna 3");
        repository.save(cliente);

        Optional<Cliente> resultado = repository.findByRutIgnoreCase("19876543-k");

        assertTrue(resultado.isPresent());
        assertEquals("Diego", resultado.get().getNombre());
        assertEquals("19876543-K", resultado.get().getRut());
    }

    @Test
    @DisplayName("deleteByRutIgnoreCase - debe eliminar al cliente utilizando su RUT")
    void testDeleteByRutIgnoreCase() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Diego");
        cliente.setApellidoPaterno("Muñoz");
        cliente.setRut("19876543-K");
        cliente.setCorreo("diego@mail.cl");
        cliente.setTelefono("+5693");
        cliente.setDireccion("Direccion 3");
        cliente.setComuna("Comuna 3");
        repository.save(cliente);

        repository.deleteByRutIgnoreCase("19876543-k");

        Optional<Cliente> resultado = repository.findByRutIgnoreCase("19876543-K");
        assertFalse(resultado.isPresent());
    }
}