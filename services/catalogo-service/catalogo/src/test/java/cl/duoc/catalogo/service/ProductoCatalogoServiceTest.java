package cl.duoc.catalogo.service;

import cl.duoc.catalogo.client.InventarioClient;
import cl.duoc.catalogo.dto.CatalogoCreateDTO;
import cl.duoc.catalogo.dto.CatalogoDTO;
import cl.duoc.catalogo.dto.InventarioDTO;
import cl.duoc.catalogo.excepciones.RecursoNoEncontradoException;
import cl.duoc.catalogo.model.ProductoCatalogo;
import cl.duoc.catalogo.repository.ProductoCatalogoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoCatalogoServiceTest {

    @Mock
    private ProductoCatalogoRepository repository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private ProductoCatalogoService service;

    @Test
    @DisplayName("obtenerTodos - debe retornar una lista con los DTOs de todos los productos localizados")
    void debeRetornarListaDeProductosDTO() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setId(1L);
        producto.setNombre("Audifonos Gamer");
        producto.setSku("AUD-71");
        producto.setPrecio(35000.0);

        when(repository.findAll()).thenReturn(Collections.singletonList(producto));

        // When
        List<CatalogoDTO> resultado = service.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("AUD-71", resultado.get(0).getSku());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId - debe retornar el DTO correspondiente cuando el ID existe y tiene stock en inventario")
    void debeRetornarProductoPorIdExistente() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setId(1L);
        producto.setNombre("Audifonos Gamer");
        producto.setSku("AUD-71");

        InventarioDTO inventarioDTO = new InventarioDTO();
        inventarioDTO.setCantidad(15);

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioClient.obtenerStockPorSku("AUD-71")).thenReturn(inventarioDTO);

        // When
        CatalogoDTO resultado = service.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Audifonos Gamer", resultado.getNombre());
        verify(repository, times(1)).findById(1L);
        verify(inventarioClient, times(1)).obtenerStockPorSku("AUD-71");
    }

    @Test
    @DisplayName("guardarProducto - debe persistir el registro si la validación remota de SKU en inventario es exitosa")
    void debeGuardarProductoExitosamente() {
        // Given
        CatalogoCreateDTO createDTO = new CatalogoCreateDTO("Audifonos Gamer", "Sonido 7.1", 35000.0, "Audio", "AUD-71", true);
        
        ProductoCatalogo productoGuardado = new ProductoCatalogo();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Audifonos Gamer");
        productoGuardado.setSku("AUD-71");

        InventarioDTO inventarioDTO = new InventarioDTO();
        inventarioDTO.setCantidad(10);

        when(inventarioClient.obtenerStockPorSku("AUD-71")).thenReturn(inventarioDTO);
        when(repository.save(any(ProductoCatalogo.class))).thenReturn(productoGuardado);

        // When
        CatalogoDTO resultado = service.guardarProducto(createDTO);

        // Then
        assertNotNull(resultado);
        assertEquals("AUD-71", resultado.getSku());
        verify(inventarioClient, times(1)).obtenerStockPorSku("AUD-71");
        verify(repository, times(1)).save(any(ProductoCatalogo.class));
    }

    @Test
    @DisplayName("actualizarProducto - debe modificar los datos del producto local si este existe por su ID")
    void debeActualizarProductoExitosamente() {
        // Given
        CatalogoCreateDTO updateDTO = new CatalogoCreateDTO("Audifonos Pro", "Sonido 7.1", 39990.0, "Audio", "AUD-71", true);
        
        ProductoCatalogo existente = new ProductoCatalogo();
        existente.setId(1L);
        existente.setNombre("Audifonos Gamer");
        existente.setSku("AUD-71");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(ProductoCatalogo.class))).thenReturn(existente);

        // When
        CatalogoDTO resultado = service.actualizarProducto(1L, updateDTO);

        // Then
        assertNotNull(resultado);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(ProductoCatalogo.class));
    }

    @Test
    @DisplayName("eliminarPorId - debe retornar true tras mandar a remover un registro existente")
    void debeEliminarPorIdCuandoExiste() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        // When
        boolean eliminado = service.eliminarPorId(1L);

        // Then
        assertTrue(eliminado);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarPorId - debe retornar false de inmediato si el ID buscado no existe")
    void debreRetornarFalseAlEliminarIdInexistente() {
        // Given
        when(repository.existsById(99L)).thenReturn(false);

        // When
        boolean eliminado = service.eliminarPorId(99L);

        // Then
        assertFalse(eliminado);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("obtenerPorId - debe arrojar RecursoNoEncontradoException ante un ID inexistente (Invariante/Regla de Negocio)")
    void debeLanzarExcepcionCuandoIdNoExiste() {
        // Given
        when(repository.findById(88L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> {
            service.obtenerPorId(88L);
        });

        verify(inventarioClient, never()).obtenerStockPorSku(anyString());
    }
}