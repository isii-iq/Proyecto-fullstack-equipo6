package cl.duoc.pedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "orden_items")
public class OrdenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long productoId;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @Positive
    private Double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonIgnore 
    private Pedido pedido;
}