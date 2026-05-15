package cl.duoc.envios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioDTO {
    private Long id;
    private Long pedidoId;
    private String direccion;
    private String comuna;
    private String estadoEnvio;
    private String numeroSeguimiento;
    private LocalDateTime fechaDespacho;
}