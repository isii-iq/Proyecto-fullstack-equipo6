package cl.duoc.cupones.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuponDTO {

   private Long id; 
   private String codigo; 
   private Integer descuento; 
   private Boolean activo; 
   private LocalDateTime fechaExpiracion;
}