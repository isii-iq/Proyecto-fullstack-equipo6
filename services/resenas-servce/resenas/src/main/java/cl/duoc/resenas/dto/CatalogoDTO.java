package cl.duoc.resenas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio; 
    private String categoria;
    private String sku; 
    private boolean disponible;
}