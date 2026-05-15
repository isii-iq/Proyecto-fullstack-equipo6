package cl.duoc.envios.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String comuna;
}