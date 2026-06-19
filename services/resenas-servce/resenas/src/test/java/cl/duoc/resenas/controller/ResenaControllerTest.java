package cl.duoc.resenas.controller;

import cl.duoc.resenas.dto.ResenaCreateDTO;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.service.ResenaService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ResenaControllerTest {

    @Mock
    private ResenaService service;

    @InjectMocks
    private ResenaController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/resenas - debe retornar 200 con la lista completa de reseñas")
    void debeRetornar200CuandoSePidenResenas() throws Exception {
        LocalDateTime fecha = LocalDateTime.now();
        when(service.obtenerTodos()).thenReturn(List.of(
            new ResenaDTO(1L, 10L, 50L, "Excelente producto", 5, fecha),
            new ResenaDTO(2L, 11L, 51L, "Malo, llego roto", 1, fecha)
        ));

        mockMvc.perform(get("/api/v1/resenas"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].comentario").value("Excelente producto"))
               .andExpect(jsonPath("$[1].calificacion").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/resenas - debe retornar 200 con una lista vacía si no existen registros")
    void debeRetornar200ConListaVacia() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/resenas"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/resenas/{id} - debe retornar 200 con la reseña si el ID existe")
    void debeRetornar200CuandoResenaExistePorId() throws Exception {
        LocalDateTime fecha = LocalDateTime.now();
        when(service.obtenerPorId(1L)).thenReturn(
            new ResenaDTO(1L, 10L, 50L, "Excelente producto", 5, fecha)
        );

        mockMvc.perform(get("/api/v1/resenas/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.comentario").value("Excelente producto"));
    }

    @Test
    @DisplayName("POST /api/v1/resenas - debe retornar 201 al crear exitosamente una reseña válida")
    void debeRetornar201AlCrearResenaValido() throws Exception {
        String jsonPayload = """
            {
                "productoId": 10,
                "clienteId": 50,
                "comentario": "Excelente producto",
                "calificacion": 5
            }
            """;

        LocalDateTime fecha = LocalDateTime.now();
        when(service.guardar(any(ResenaCreateDTO.class))).thenReturn(
            new ResenaDTO(1L, 10L, 50L, "Excelente producto", 5, fecha)
        );

        mockMvc.perform(post("/api/v1/resenas")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonPayload))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.comentario").value("Excelente producto"));
    }

    @Test
    @DisplayName("PUT /api/v1/resenas/{id} - debe retornar 200 tras actualizar con éxito una reseña")
    void debeRetornar200AlActualizarResena() throws Exception {
        String jsonPayload = """
            {
                "productoId": 10,
                "clienteId": 50,
                "comentario": "Comentario modificado",
                "calificacion": 4
            }
            """;

        LocalDateTime fecha = LocalDateTime.now();
        when(service.actualizar(eq(1L), any(ResenaCreateDTO.class))).thenReturn(
            new ResenaDTO(1L, 10L, 50L, "Comentario modificado", 4, fecha)
        );

        mockMvc.perform(put("/api/v1/resenas/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonPayload))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.comentario").value("Comentario modificado"))
               .andExpect(jsonPath("$.calificacion").value(4));
    }

    @Test
    @DisplayName("DELETE /api/v1/resenas/{id} - debe retornar 200 si la remoción por ID es exitosa")
    void debeRetornar200AlEliminarPorIdExistente() throws Exception {
        when(service.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/resenas/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mensaje").value("Reseña eliminada con éxito"));
    }

    @Test
    @DisplayName("DELETE /api/v1/resenas/{id} - debe retornar 404 si el ID no es encontrado")
    void debeRetornar404AlEliminarIdInexistente() throws Exception {
        when(service.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/resenas/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("No se encontró la reseña con ID: 99"));
    }
}