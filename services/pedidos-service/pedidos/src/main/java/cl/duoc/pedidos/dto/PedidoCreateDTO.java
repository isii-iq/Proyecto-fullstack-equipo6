package cl.duoc.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class PedidoCreateDTO {
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El total es obligatorio")
    private Double total;

    @NotNull(message = "El pedido debe tener items")
    @Size(min = 1, message = "Debe incluir al menos un producto")
    private List<OrdenItemDTO> items; // Cambiado para recibir los productos
}