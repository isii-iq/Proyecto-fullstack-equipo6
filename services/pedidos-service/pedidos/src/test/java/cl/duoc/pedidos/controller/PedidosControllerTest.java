package cl.duoc.pedidos.controller;

import cl.duoc.pedidos.dto.PedidoCreateDTO;
import cl.duoc.pedidos.excepciones.GlobalExceptionHandler;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.service.PedidoService;
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
class PedidoControllerTest {

    @Mock
    private PedidoService service;

    @InjectMocks
    private PedidoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── GET /api/v1/pedidos ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/pedidos - debe retornar 200 con la lista de pedidos")
    void debeRetornar200CuandoSePidenPedidos() throws Exception {
        // Given
        // NOTA: Ajusta los parámetros del constructor de tu modelo Pedido si son distintos
        Pedido p1 = new Pedido(); p1.setId(1L); p1.setEstado("PENDIENTE");
        Pedido p2 = new Pedido(); p2.setId(2L); p2.setEstado("PROCESANDO");
        
        when(service.obtenerTodos()).thenReturn(List.of(p1, p2));

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /api/v1/pedidos - debe retornar 200 con lista vacía cuando no hay registros")
    void debeRetornar200ConListaVacia() throws Exception {
        // Given
        when(service.obtenerTodos()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/v1/pedidos/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/pedidos/{id} - debe retornar 200 cuando el pedido existe")
    void debeRetornar200CuandoPedidoExiste() throws Exception {
        // Given
        Pedido pedido = new Pedido();
        pedido.setId(10L);
        pedido.setEstado("ENVIADO");
        
        when(service.obtenerPorId(10L)).thenReturn(pedido);

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos/10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(10))
               .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    @DisplayName("GET /api/v1/pedidos/{id} - debe retornar 404 cuando el pedido no existe (lanza excepción)")
    void debeRetornar404CuandoPedidoNoExiste() throws Exception {
        // Given
        when(service.obtenerPorId(999L)).thenThrow(new RuntimeException("Pedido no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/v1/pedidos/999"))
               .andExpect(status().isNotFound());
    }

    // ── POST /api/v1/pedidos ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/pedidos - debe retornar 201 al crear un pedido válido")
    void debeRetornar201AlCrearPedido() throws Exception {
        // Given
        // Modifica este JSON según las propiedades reales de tu PedidoCreateDTO
        String jsonBody = """
            {
                "clienteId": 1,
                "total": 25000
            }
            """;
        
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setId(1L);
        nuevoPedido.setEstado("PENDIENTE");

        when(service.guardar(any(PedidoCreateDTO.class))).thenReturn(nuevoPedido);

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("POST /api/v1/pedidos - debe retornar 400 cuando ocurre un error en el servicio")
    void debeRetornar400CuandoFallaElGuardado() throws Exception {
        // Given
        String jsonBody = "{}";
        when(service.guardar(any(PedidoCreateDTO.class))).thenThrow(new RuntimeException("Error de validación interna"));

        // When & Then
        mockMvc.perform(post("/api/v1/pedidos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Error de validación interna"));
    }

    // ── PUT /api/v1/pedidos/{id}/estado ────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/v1/pedidos/{id}/estado - debe retornar 200 al actualizar estado con éxito")
    void debeRetornar200AlActualizarEstado() throws Exception {
        // Given
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(1L);
        pedidoActualizado.setEstado("ENTREGADO");

        when(service.actualizarEstado(eq(1L), eq("ENTREGADO"))).thenReturn(pedidoActualizado);

        // When & Then
        mockMvc.perform(put("/api/v1/pedidos/1/estado")
               .param("estado", "ENTREGADO"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }

    @Test
    @DisplayName("PUT /api/v1/pedidos/{id}/estado - debe retornar 404 si el pedido a actualizar no existe")
    void debeRetornar404AlActualizarEstadoInvalido() throws Exception {
        // Given
        when(service.actualizarEstado(eq(999L), eq("ENVIADO"))).thenThrow(new RuntimeException("Pedido no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/v1/pedidos/999/estado")
               .param("estado", "ENVIADO"))
               .andExpect(status().isNotFound())
               .andExpect(content().string("Pedido no encontrado"));
    }

    // ── DELETE /api/v1/pedidos/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/v1/pedidos/{id} - debe retornar 200 si el pedido se elimina correctamente")
    void debeRetornar200AlEliminarExitosamente() throws Exception {
        // Given
        when(service.eliminar(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/pedidos/1"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/pedidos/{id} - debe retornar 404 si el pedido a eliminar no existe")
    void debeRetornar404AlEliminarInexistente() throws Exception {
        // Given
        when(service.eliminar(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/pedidos/999"))
               .andExpect(status().isNotFound());
    }
}