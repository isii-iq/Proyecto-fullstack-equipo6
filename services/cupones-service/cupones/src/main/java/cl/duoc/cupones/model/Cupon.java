package cl.duoc.cupones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cupones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código del cupón no puede estar vacío")
    @Size(min = 4, max = 20, message = "El código debe tener entre 4 y 20 caracteres")
    @Column(unique = true, nullable = false)
    private String codigo;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.01", message = "El descuento mínimo es 1%")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
    private Double porcentajeDescuento;

    @NotNull(message = "El ID del pedido es obligatorio para vincular el cupón")
    private Long pedidoId;

    private Boolean usado = false;
}