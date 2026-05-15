package cl.duoc.cupones.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuponCreateDTO {


    @NotBlank(message = "El código del cupón es obligatorio")
    @Column(unique = true, nullable = false)
    private String codigo;

    @NotNull(message = "El descuento es obligatorio")
    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 100, message = "El descuento máximo es 100%")
    private Integer descuento;

    @NotNull(message = "El estado del cupón es obligatorio")
    private Boolean activo;

    @NotNull(message = "La fecha de expiración es obligatoria")
    private LocalDateTime fechaExpiracion;
}

