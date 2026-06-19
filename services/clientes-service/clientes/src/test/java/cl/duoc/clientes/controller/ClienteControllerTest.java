package cl.duoc.clientes.controller;

import cl.duoc.clientes.dto.ClienteCreateDTO;
import cl.duoc.clientes.dto.ClienteDTO;
import cl.duoc.clientes.service.ClienteService;
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
class ClienteControllerTest {

    @Mock
    private ClienteService service;

    @InjectMocks
    private ClienteController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/clientes - debe retornar 200 con la lista completa de clientes")
    void debeRetornar200CuandoSePidenClientes() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(
            new ClienteDTO(1L, "Juan", "Perez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago"),
            new ClienteDTO(2L, "Maria", "Gonzalez", "9876543-2", "maria@mail.cl", "+5692", "Direccion 2", "Providencia")
        ));

        mockMvc.perform(get("/api/v1/clientes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].nombre").value("Juan"))
               .andExpect(jsonPath("$[1].rut").value("9876543-2"));
    }

    @Test
    @DisplayName("GET /api/v1/clientes - debe retornar 200 con una lista vacía si no existen registros")
    void debeRetornar200ConListaVacia() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clientes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} - debe retornar 200 con el cliente si el ID existe")
    void debeRetornar200CuandoClienteExistePorId() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(
            new ClienteDTO(1L, "Juan", "Perez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago")
        );

        mockMvc.perform(get("/api/v1/clientes/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @DisplayName("POST /api/v1/clientes - debe retornar 201 al crear exitosamente un cliente válido")
    void debeRetornar201AlCrearClienteValido() throws Exception {
        String jsonPayload = """
            {
                "nombre": "Juan",
                "apellidoPaterno": "Perez",
                "rut": "12345678-9",
                "correo": "juan@mail.cl",
                "telefono": "+5691",
                "direccion": "Direccion 1",
                "comuna": "Santiago"
            }
            """;

        when(service.guardarCliente(any(ClienteCreateDTO.class))).thenReturn(
            new ClienteDTO(1L, "Juan", "Perez", "12345678-9", "juan@mail.cl", "+5691", "Direccion 1", "Santiago")
        );

        mockMvc.perform(post("/api/v1/clientes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonPayload))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @DisplayName("PUT /api/v1/clientes/{id} - debe retornar 200 tras actualizar con éxito un cliente")
    void debeRetornar200AlActualizarCliente() throws Exception {
        String jsonPayload = """
            {
                "nombre": "Juan Carlos",
                "apellidoPaterno": "Perez",
                "rut": "12345678-9",
                "correo": "juan@mail.cl",
                "telefono": "+5692",
                "direccion": "Direccion Nueva",
                "comuna": "Providencia"
            }
            """;

        when(service.actualizarCliente(eq(1L), any(ClienteCreateDTO.class))).thenReturn(
            new ClienteDTO(1L, "Juan Carlos", "Perez", "12345678-9", "juan@mail.cl", "+5692", "Direccion Nueva", "Providencia")
        );

        mockMvc.perform(put("/api/v1/clientes/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonPayload))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Juan Carlos"))
               .andExpect(jsonPath("$.comuna").value("Providencia"));
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/{id} - debe retornar 200 si la remoción por ID es exitosa")
    void debeRetornar200AlEliminarPorIdExistente() throws Exception {
        when(service.eliminarPorId(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clientes/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mensaje").value("Cliente eliminado con éxito"));
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/{id} - debe retornar 404 si el ID no es encontrado")
    void debeRetornar404AlEliminarIdInexistente() throws Exception {
        when(service.eliminarPorId(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/clientes/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("No se encontró el cliente con ID: 99"));
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/rut/{rut} - debe retornar 200 si la remoción por RUT es exitosa")
    void debeRetornar200AlEliminarPorRutExistente() throws Exception {
        when(service.eliminarPorRut("12345678-9")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clientes/rut/12345678-9"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mensaje").value("Cliente con RUT 12345678-9 eliminado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/rut/{rut} - debe retornar 404 si el RUT no existe")
    void debeRetornar404AlEliminarRutInexistente() throws Exception {
        when(service.eliminarPorRut("11111111-1")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/clientes/rut/11111111-1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("RUT no encontrado"));
    }
}