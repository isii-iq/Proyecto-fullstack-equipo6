package cl.duoc.resenas.service;

import cl.duoc.resenas.client.CatalogoClient;
import cl.duoc.resenas.client.ClienteClient;
import cl.duoc.resenas.dto.CatalogoDTO;
import cl.duoc.resenas.dto.ResenaCreateDTO;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.excepciones.RecursoNoEncontradoException;
import cl.duoc.resenas.excepciones.ServicioNoDisponibleException;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.repository.ResenaRepository;
import feign.FeignException;
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
class ResenaServiceTest {

    @Mock
    private ResenaRepository repository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private ResenaService service;

    @Test
    @DisplayName("obtenerTodos - debe retornar una lista con los DTOs de todas las reseñas")
    void debeRetornarListaDeResenasDTO() {
        Resena resena = new Resena(1L, 10L, 50L, "Excelente", 5, LocalDateTime.now());
        when(repository.findAll()).thenReturn(Collections.singletonList(resena));

        List<ResenaDTO> resultado = service.obtenerTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Excelente", resultado.get(0).getComentario());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId - debe retornar el DTO correspondiente cuando el ID existe")
    void debeRetornarResenaPorIdExistente() {
        Resena resena = new Resena(1L, 10L, 50L, "Muy bueno", 4, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(resena));

        ResenaDTO resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Muy bueno", resultado.getComentario());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar RecursoNoEncontradoException cuando el ID no existe")
    void debeLanzarExcepcionCuandoIdNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("actualizar - debe modificar los datos de la reseña si existe")
    void debeActualizarResenaExitosamente() {
        ResenaCreateDTO dto = new ResenaCreateDTO();
        dto.setComentario("Comentario modificado");
        dto.setCalificacion(3);

        Resena existente = new Resena(1L, 10L, 50L, "Comentario viejo", 5, LocalDateTime.now());
        
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Resena.class))).thenReturn(existente);

        ResenaDTO resultado = service.actualizar(1L, dto);

        assertNotNull(resultado);
        assertEquals("Comentario modificado", resultado.getComentario());
        assertEquals(3, resultado.getCalificacion());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Resena.class));
    }

    @Test
    @DisplayName("guardar - debe registrar la reseña exitosamente si las validaciones Feign son correctas")
    void debeGuardarResenaExitosamente() {
        ResenaCreateDTO dto = new ResenaCreateDTO();
        dto.setProductoId(10L);
        dto.setClienteId(50L);
        dto.setComentario("Genial");
        dto.setCalificacion(5);

        CatalogoDTO productoDTO = new CatalogoDTO();
        productoDTO.setDisponible(true);

        Resena guardada = new Resena(1L, 10L, 50L, "Genial", 5, LocalDateTime.now());

        when(clienteClient.getClienteById(50L)).thenReturn(null);
        when(catalogoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(repository.save(any(Resena.class))).thenReturn(guardada);

        ResenaDTO resultado = service.guardar(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(clienteClient, times(1)).getClienteById(50L);
        verify(catalogoClient, times(1)).obtenerProductoPorId(10L);
        verify(repository, times(1)).save(any(Resena.class));
    }

    @Test
    @DisplayName("guardar - debe lanzar RecursoNoEncontradoException cuando Feign indica que el cliente no existe")
    void debeLanzarExcepcionCuandoClienteNoExiste() {
        ResenaCreateDTO dto = new ResenaCreateDTO();
        dto.setClienteId(50L);

        FeignException.NotFound feignException = mock(FeignException.NotFound.class);
        when(clienteClient.getClienteById(50L)).thenThrow(feignException);

        assertThrows(RecursoNoEncontradoException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("guardar - debe lanzar ServicioNoDisponibleException cuando ms-clientes falla de forma genérica")
    void debeLanzarExcepcionCuandoMsClientesNoDisponible() {
        ResenaCreateDTO dto = new ResenaCreateDTO();
        dto.setClienteId(50L);

        FeignException feignException = mock(FeignException.class);
        when(clienteClient.getClienteById(50L)).thenThrow(feignException);

        assertThrows(ServicioNoDisponibleException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("guardar - debe lanzar RecursoNoEncontradoException cuando el producto no está disponible")
    void debeLanzarExcepcionCuandoProductoNoDisponible() {
        ResenaCreateDTO dto = new ResenaCreateDTO();
        dto.setProductoId(10L);
        dto.setClienteId(50L);

        CatalogoDTO productoDTO = new CatalogoDTO();
        productoDTO.setDisponible(false);

        when(clienteClient.getClienteById(50L)).thenReturn(null);
        when(catalogoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        assertThrows(RecursoNoEncontradoException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("eliminar - debe retornar true tras remover una reseña existente")
    void debeEliminarCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        boolean eliminado = service.eliminar(1L);

        assertTrue(eliminado);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - debe retornar false si la reseña no existe")
    void debeRetornarFalseAlEliminarInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean eliminado = service.eliminar(99L);

        assertFalse(eliminado);
        verify(repository, never()).deleteById(anyLong());
    }
}