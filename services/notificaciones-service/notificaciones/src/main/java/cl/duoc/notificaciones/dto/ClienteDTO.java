package cl.duoc.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String rut;
    private String correo;
    private String telefono;
    private String direccion;
    private String comuna;
}