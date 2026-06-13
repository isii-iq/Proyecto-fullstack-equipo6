package cl.duoc.inventario.service;

import cl.duoc.inventario.dto.InventarioCreateDTO;
import cl.duoc.inventario.dto.InventarioDTO;
import cl.duoc.inventario.excepciones.RecursoNoEncontradoException;
import cl.duoc.inventario.model.Producto;
import cl.duoc.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService productoService;

    // ── obtenerTodos ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodos - debe retornar lista de DTOs cuando existen registros")
    void debeRetornarListaDeProductos() {
        // Given
        List<Producto> productosSimulados = List.of(
            new Producto(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00")),
            new Producto(2L, "Mouse Logitech", "Accesorios", 25, "LOG-98765-MX", new BigDecimal("89990.00"))
        );
        when(repository.findAll()).thenReturn(productosSimulados);

        // When
        List<InventarioDTO> resultado = productoService.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop Lenovo", resultado.get(0).getNombre());
        assertEquals("LNV-12345-MX", resultado.get(0).getSku());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodos - debe retornar lista vacía cuando no hay productos")
    void debeRetornarListaVaciaSiNoHayProductos() {
        // Given
        when(repository.findAll()).thenReturn(List.of());

        // When
        List<InventarioDTO> resultado = productoService.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ── obtenerPorId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId - debe retornar el DTO correcto cuando el producto existe")
    void debeRetornarProductoPorId() {
        // Given
        Producto producto = new Producto(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        InventarioDTO resultado = productoService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop Lenovo", resultado.getNombre());
        assertEquals(10, resultado.getCantidad());
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar RecursoNoEncontradoException cuando el ID no existe")
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () ->
            productoService.obtenerPorId(999L)
        );
    }

    // ── obtenerPorSku ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorSku - debe retornar el DTO correcto cuando el SKU existe")
    void debeRetornarProductoPorSku() {
        // Given
        String skuBuscado = "lnv-12345-mx";
        Producto producto = new Producto(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"));
        when(repository.findBySkuIgnoreCase(skuBuscado)).thenReturn(Optional.of(producto));

        // When
        InventarioDTO resultado = productoService.obtenerPorSku(skuBuscado);

        // Then
        assertNotNull(resultado);
        assertEquals("LNV-12345-MX", resultado.getSku());
        assertEquals("Laptop Lenovo", resultado.getNombre());
    }

    // ── guardarProducto ────────────────────────────────────────────────────────

    @Test
    @DisplayName("guardarProducto - debe persistir y retornar el producto con ID generado")
    void debeCrearProductoCorrectamente() {
        // Given
        InventarioCreateDTO dto = new InventarioCreateDTO();
        dto.setSku("RED-44332-RGB");
        dto.setNombre("Teclado Mecánico");
        dto.setCantidad(15);
        dto.setPrecio(new BigDecimal("59990.00"));
        dto.setCategoria("Accesorios");

        Producto guardado = new Producto(3L, "Teclado Mecánico", "Accesorios", 15, "RED-44332-RGB", new BigDecimal("59990.00"));
        when(repository.save(any(Producto.class))).thenReturn(guardado);

        // When
        InventarioDTO resultado = productoService.guardarProducto(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Teclado Mecánico", resultado.getNombre());
        verify(repository, times(1)).save(any(Producto.class));
    }

    // ── eliminarPorId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarPorId - debe retornar false al intentar eliminar un ID inexistente")
    void debeRetornarFalseAlEliminarProductoInexistente() {
        // Given
        when(repository.existsById(999L)).thenReturn(false);

        // When
        boolean resultado = productoService.eliminarPorId(999L);

        // Then
        assertFalse(resultado);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminarPorId - debe retornar true e invocar deleteById cuando el producto existe")
    void debeEliminarProductoExistente() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = productoService.eliminarPorId(1L);

        // Then
        assertTrue(resultado);
        verify(repository, times(1)).deleteById(1L);
    }

    // ── eliminarPorSKU ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarPorSKU - debe retornar true y borrar el objeto cuando el SKU existe")
    void debeEliminarPorSkuCorrectamente() {
        // Given
        String skuTarget = "LNV-12345-MX";
        Producto productoAEliminar = new Producto(1L, "Laptop Lenovo", "Computación", 10, skuTarget, new BigDecimal("649990.00"));
        
        when(repository.existsBySkuIgnoreCase(skuTarget)).thenReturn(true);
        when(repository.findBySkuIgnoreCase(skuTarget)).thenReturn(Optional.of(productoAEliminar));

        // When
        boolean resultado = productoService.eliminarPorSKU(skuTarget);

        // Then
        assertTrue(resultado);
        verify(repository, times(1)).delete(productoAEliminar);
    }
}