package cl.duoc.pedidos.dto;

import lombok.Data;

@Data
public class ProductoCatalogoDTO {
private Long id;
private String nombre;
private Double precio;
private String sku;
private boolean disponible;
}