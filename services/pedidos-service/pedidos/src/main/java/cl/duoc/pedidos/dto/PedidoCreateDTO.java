package cl.duoc.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PedidoCreateDTO {
    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;

    @NotEmpty(message = "El pedido debe tener al menos un ítem")
    @Valid
    private List<ItemCreateDTO> items;
}