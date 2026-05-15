package cl.duoc.catalogo.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private int cantidad;
    private String sku;
    private BigDecimal precio;
}