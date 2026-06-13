package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.InventarioCreateDTO;
import cl.duoc.inventario.dto.InventarioDTO;
import cl.duoc.inventario.excepciones.*;
import cl.duoc.inventario.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    private MockMvc mockMvc;
    private static final String BASE_URL = "/api/v1/productos";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()) 
                .build();
    }

    // ── GET /api/v1/productos ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/productos - debe retornar 200 con la lista de productos")
    void debeRetornar200CuandoSePidenProductos() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(
            new InventarioDTO(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00")),
            new InventarioDTO(2L, "Mouse Logitech", "Accesorios", 25, "LOG-98765-MX", new BigDecimal("89990.00"))
        ));

        // When & Then
        mockMvc.perform(get(BASE_URL))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].nombre").value("Laptop Lenovo"))
               .andExpect(jsonPath("$[0].sku").value("LNV-12345-MX"))
               .andExpect(jsonPath("$[1].categoria").value("Accesorios"));
    }

    // ── GET /api/v1/productos/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/productos/{id} - debe retornar 200 cuando el producto existe")
    void debeRetornar200CuandoProductoExiste() throws Exception {
        // Given
        when(service.obtenerPorId(1L)).thenReturn(
            new InventarioDTO(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"))
        );

        // When & Then
        mockMvc.perform(get(BASE_URL + "/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Laptop Lenovo"));
    }

    // ── POST /api/v1/productos ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/productos - debe retornar 201 al crear un producto válido")
    void debeRetornar201AlCrearProducto() throws Exception {
        // Given 
        String jsonInput = """
            {
                "nombre": "Audífonos Bluetooth",
                "categoria": "Accesorios",
                "cantidad": 20,
                "sku": "AUD-11223-BT",
                "precio": 79990.00
            }
            """;

        when(service.guardarProducto(any(InventarioCreateDTO.class))).thenReturn(
            new InventarioDTO(7L, "Audífonos Bluetooth", "Accesorios", 20, "AUD-11223-BT", new BigDecimal("79990.00"))
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonInput))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(7))
               .andExpect(jsonPath("$.nombre").value("Audífonos Bluetooth"))
               .andExpect(jsonPath("$.sku").value("AUD-11223-BT"));
    }

    // ── PUT /api/v1/productos/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/v1/productos/{id} - debe retornar 200 al actualizar exitosamente")
    void debeRetornar200AlActualizarProducto() throws Exception {
        // Given
        String jsonInput = """
            {
                "nombre": "Laptop Lenovo Pro",
                "categoria": "Computación",
                "cantidad": 12,
                "sku": "LNV-12345-MX",
                "precio": 699990.00
            }
            """;

        when(service.actualizarProducto(eq(1L), any(InventarioCreateDTO.class))).thenReturn(
            new InventarioDTO(1L, "Laptop Lenovo Pro", "Computación", 12, "LNV-12345-MX", new BigDecimal("699990.00"))
        );

        // When & Then
        mockMvc.perform(put(BASE_URL + "/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonInput))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Laptop Lenovo Pro"))
               .andExpect(jsonPath("$.cantidad").value(12));
    }

    // ── DELETE /api/v1/productos/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/v1/productos/{id} - debe retornar 200 si elimina correctamente")
    void debeRetornar200AlEliminarPorId() throws Exception {
        // Given
        when(service.eliminarPorId(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mensaje").value("Producto eliminado con éxito"));
    }

    @Test
    @DisplayName("DELETE /api/v1/productos/{id} - debe retornar 404 si el ID no existe")
    void debeRetornar404AlEliminarIdInexistente() throws Exception {
        // Given
        when(service.eliminarPorId(99L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("No se encontró el producto con ID: 99"));
    }

    // ── GET /api/v1/productos/validar/{id}/{cantidad} ─────────────────────────

    @Test
    @DisplayName("GET /api/v1/productos/validar/... - debe retornar true si hay stock suficiente")
    void debeRetornarTrueCuandoExisteStock() throws Exception {
        // Given
        when(service.obtenerPorId(1L)).thenReturn(
            new InventarioDTO(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"))
        );

        // When & Then 
        mockMvc.perform(get(BASE_URL + "/validar/1/5"))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/validar/... - debe retornar false si la cantidad pedida supera al stock")
    void debeRetornarFalseCuandoStockEsInsuficiente() throws Exception {
        // Given
        when(service.obtenerPorId(1L)).thenReturn(
            new InventarioDTO(1L, "Laptop Lenovo", "Computación", 10, "LNV-12345-MX", new BigDecimal("649990.00"))
        );

        // When & Then 
        mockMvc.perform(get(BASE_URL + "/validar/1/15"))
               .andExpect(status().isOk())
               .andExpect(content().string("false"));
    }
}