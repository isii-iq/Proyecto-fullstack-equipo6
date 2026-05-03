package cl.duoc.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Min(value = 0, message = "La cantidad no puede ser menor a cero")
    private int cantidad;

    @NotBlank(message = "El código SKU es obligatorio")
    @Column(unique = true)
    private String sku;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precio;

}