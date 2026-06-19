package cl.duoc.clientes.service;

import cl.duoc.clientes.dto.ClienteCreateDTO;
import cl.duoc.clientes.dto.ClienteDTO;
import cl.duoc.clientes.excepciones.RecursoNoEncontradoException;
import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    @DisplayName("obtenerTodos - debe retornar una lista con los DTOs de todos los clientes")
    void debeRetornarListaDeClientesDTO() {
        Cliente cliente = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago", true, LocalDateTime.now());
        when(repository.findAll()).thenReturn(Collections.singletonList(cliente));

        List<ClienteDTO> resultado = service.obtenerTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("12345678-9", resultado.get(0).getRut());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId - debe retornar el DTO correspondiente cuando el ID existe")
    void debeRetornarClientePorIdExistente() {
        Cliente cliente = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago", true, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDTO resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar RecursoNoEncontradoException ante un ID inexistente")
    void debeLanzarExcepcionCuandoIdNoExiste() {
        when(repository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(88L));
        verify(repository, times(1)).findById(88L);
    }

    @Test
    @DisplayName("guardarCliente - debe persistir el registro del cliente correctamente")
    void debeGuardarClienteExitosamente() {
        ClienteCreateDTO createDTO = new ClienteCreateDTO("Juan", "Pérez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago");
        Cliente clienteGuardado = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago", true, LocalDateTime.now());
        when(repository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        ClienteDTO resultado = service.guardarCliente(createDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("12345678-9", resultado.getRut());
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("actualizarCliente - debe modificar los datos del cliente si este existe por su ID")
    void debeActualizarClienteExitosamente() {
        ClienteCreateDTO updateDTO = new ClienteCreateDTO("Juan Carlos", "Pérez", "12345678-9", "juan@mail.cl", "+5692", "Direccion Nueva", "Providencia");
        Cliente existente = new Cliente(1L, "Juan", "Pérez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago", true, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenReturn(existente);

        ClienteDTO resultado = service.actualizarCliente(1L, updateDTO);

        assertNotNull(resultado);
        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals("Providencia", resultado.getComuna());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("eliminarPorId - debe retornar true tras remover un registro existente")
    void debeEliminarPorIdCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        boolean eliminado = service.eliminarPorId(1L);

        assertTrue(eliminado);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarPorId - debe retornar false si el ID buscado no existe")
    void debeRetornarFalseAlEliminarIdInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean eliminado = service.eliminarPorId(99L);

        assertFalse(eliminado);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("eliminarPorRut - debe retornar true tras remover un cliente por su RUT")
    void debeEliminarPorRutCuandoExiste() {
        when(repository.existsByRutIgnoreCase("12345678-9")).thenReturn(true);
        doNothing().when(repository).deleteByRutIgnoreCase("12345678-9");

        boolean eliminado = service.eliminarPorRut("12345678-9");

        assertTrue(eliminado);
        verify(repository, times(1)).deleteByRutIgnoreCase("12345678-9");
    }

    @Test
    @DisplayName("eliminarPorRut - debe retornar false si el RUT buscado no existe")
    void debeRetornarFalseAlEliminarRutInexistente() {
        when(repository.existsByRutIgnoreCase("11111111-1")).thenReturn(false);

        boolean eliminado = service.eliminarPorRut("11111111-1");

        assertFalse(eliminado);
        verify(repository, never()).deleteByRutIgnoreCase(anyString());
    }
}