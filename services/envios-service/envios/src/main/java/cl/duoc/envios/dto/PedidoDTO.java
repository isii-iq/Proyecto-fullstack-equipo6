package cl.duoc.envios.dto;

import lombok.Data;

@Data
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private String estado;
}