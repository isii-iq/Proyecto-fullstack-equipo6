package cl.duoc.pagos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.1", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; 

    @NotBlank(message = "El estado es obligatorio")
    private String estado; 

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @NotBlank(message = "La boleta es obligatoria")
    @Column(unique = true)
    private String numeroBoleta; 

    @PrePersist
    protected void onCreate() {
        this.fechaPago = LocalDateTime.now();
    }
}