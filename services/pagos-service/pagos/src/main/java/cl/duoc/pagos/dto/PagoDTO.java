package cl.duoc.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private Long pedidoId;
    private Double monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaTransaccion;
}