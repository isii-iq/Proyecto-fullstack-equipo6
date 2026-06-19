package cl.duoc.catalogo.controller;

import cl.duoc.catalogo.dto.CatalogoCreateDTO;
import cl.duoc.catalogo.dto.CatalogoDTO;
import cl.duoc.catalogo.excepciones.RecursoNoEncontradoException;
import cl.duoc.catalogo.service.ProductoCatalogoService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoCatalogoControllerTest {

    @Mock
    private ProductoCatalogoService service;

    @InjectMocks
    private ProductoCatalogoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // ── GET /api/v1/productos ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/productos - debe retornar 200 con la lista completa de productos")
    void debeRetornar200CuandoSePidenProductos() throws Exception {
        // Given
        when(service.obtenerTodos()).thenReturn(List.of(
            new CatalogoDTO(1L, "Teclado Mecanico", "Switch Red", 45000.0, "Perifericos", "TEC-123", true),
            new CatalogoDTO(2L, "Mouse Optico", "Gamer 16000DPI", 25000.0, "Perifericos", "MOU-987", true)
        ));

        // When & Then
        mockMvc.perform(get("/api/v1/productos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].nombre").value("Teclado Mecanico"))
               .andExpect(jsonPath("$[1].sku").value("MOU-987"));
    }

    @Test
    @DisplayName("GET /api/v1/productos - debe retornar 200 con una lista vacía si no existen registros")
    void debeRetornar200ConListaVacia() throws Exception {
        // Given
        when(service.obtenerTodos()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/productos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/v1/productos/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/productos/{id} - debe retornar 200 con el producto si el ID existe")
    void debeRetornar200CuandoProductoExistePorId() throws Exception {
        // Given
        when(service.obtenerPorId(1L)).thenReturn(
            new CatalogoDTO(1L, "Teclado Mecanico", "Switch Red", 45000.0, "Perifericos", "TEC-123", true)
        );

        // When & Then
        mockMvc.perform(get("/api/v1/productos/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Teclado Mecanico"));
    }

    // ── POST /api/v1/productos ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/productos - debe retornar 201 al crear exitosamente un producto válido")
    void debeRetornar201AlCrearProductoValido() throws Exception {
        // Given
        String jsonPayload = """
            {
                "nombre": "Teclado Mecanico",
                "descripcion": "Switch Red",
                "precio": 45000.0,
                "categoria": "Perifericos",
                "sku": "TEC-123",
                "disponible": true
            }
            """;

        when(service.guardarProducto(any(CatalogoCreateDTO.class))).thenReturn(
            new CatalogoDTO(1L, "Teclado Mecanico", "Switch Red", 45000.0, "Perifericos", "TEC-123", true)
        );

        // When & Then
        mockMvc.perform(post("/api/v1/productos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonPayload))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Teclado Mecanico"));
    }

    // ── PUT /api/v1/productos/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/v1/productos/{id} - debe retornar 200 tras actualizar con éxito un producto")
    void debeRetornar200AlActualizarProducto() throws Exception {
        // Given
        String jsonPayload = """
            {
                "nombre": "Teclado Pro",
                "descripcion": "Switch Blue",
                "precio": 49990.0,
                "categoria": "Perifericos",
                "sku": "TEC-123",
                "disponible": true
            }
            """;

        when(service.actualizarProducto(eq(1L), any(CatalogoCreateDTO.class))).thenReturn(
            new CatalogoDTO(1L, "Teclado Pro", "Switch Blue", 49990.0, "Perifericos", "TEC-123", true)
        );

        // When & Then
        mockMvc.perform(put("/api/v1/productos/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonPayload))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Teclado Pro"))
               .andExpect(jsonPath("$.precio").value(49990.0));
    }

    // ── DELETE /api/v1/productos/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/v1/productos/{id} - debe retornar 200 si la remoción por ID es exitosa")
    void debeRetornar200AlEliminarPorIdExistente() throws Exception {
        // Given
        when(service.eliminarPorId(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/productos/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mensaje").value("Producto eliminado con éxito"));
    }

    @Test
    @DisplayName("DELETE /api/v1/productos/{id} - debe retornar 404 si el ID no es encontrado")
    void debeRetornar404AlEliminarIdInexistente() throws Exception {
        // Given
        when(service.eliminarPorId(99L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/productos/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("No se encontró el producto con ID: 99"));
    }

    // ── DELETE /api/v1/productos/sku/{sku} ──────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/v1/productos/sku/{sku} - debe retornar 200 si la remoción por SKU es exitosa")
    void debeRetornar200AlEliminarPorSkuExistente() throws Exception {
        // Given
        when(service.eliminarPorSKU("TEC-123")).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/productos/sku/TEC-123"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mensaje").value("Producto con SKU TEC-123 eliminado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/productos/sku/{sku} - debe retornar 404 si el SKU no existe")
    void debeRetornar404AlEliminarSkuInexistente() throws Exception {
        // Given
        when(service.eliminarPorSKU("SKU-FALSO")).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/productos/sku/SKU-FALSO"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("SKU no encontrado"));
    }
}