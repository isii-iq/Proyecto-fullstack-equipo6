package cl.duoc.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {

    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    private boolean leido;
    private LocalDateTime fechaCreacion;
}