package cl.duoc.inventario.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private int cantidad;
    private String sku; 
    private BigDecimal precio; 
  
   
   
}