package cl.duoc.pagos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a cero")
    @Column(nullable = false)
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio (ej: DEBITO, CREDITO)")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @NotBlank(message = "El estado del pago es obligatorio")
    @Column(nullable = false)
    private String estado;

    @NotNull(message = "La fecha de transacción es obligatoria")
    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @PrePersist
    protected void onCreate() {
        this.fechaTransaccion = LocalDateTime.now();
    }
}