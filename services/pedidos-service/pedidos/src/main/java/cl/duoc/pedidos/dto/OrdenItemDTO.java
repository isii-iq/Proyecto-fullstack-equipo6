package cl.duoc.pedidos.dto;

import lombok.Data;

@Data
public class OrdenItemDTO {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
}