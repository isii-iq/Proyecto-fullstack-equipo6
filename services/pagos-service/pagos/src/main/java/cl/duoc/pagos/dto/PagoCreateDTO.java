package cl.duoc.pagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoCreateDTO {
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "El método de pago es obligatorio (DEBITO/CREDITO)")
    private String metodoPago;
}