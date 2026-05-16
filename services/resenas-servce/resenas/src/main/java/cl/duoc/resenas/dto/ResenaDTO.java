package cl.duoc.resenas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaDTO {

    private Long id;
    private Long productoId;
    private Long clienteId;
    private String comentario;
    private Integer calificacion;
    private LocalDateTime fechaCreacion;
}