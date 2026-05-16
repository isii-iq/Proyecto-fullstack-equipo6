package cl.duoc.carrito.dto;

import lombok.Data;

@Data
public class CarritoCreateDTO {
    private Long clienteId;
    private Long productoId;
    private Integer cantidad;
}