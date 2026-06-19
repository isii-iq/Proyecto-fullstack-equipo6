package cl.duoc.pedidos.repository;

import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.model.OrdenItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository repository;

    @Test
    @DisplayName("save - debe persistir el pedido con sus items y ejecutar el @PrePersist")
    void debePersistirPedidoYAsignarIdGenerado() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setClienteId(101L);
        pedido.setTotal(35000.0);
        
        OrdenItem item = new OrdenItem();
        item.setProductoId(1L);
        item.setCantidad(2);
        item.setPrecioUnitario(17500.0);
        item.setPedido(pedido);
        
        pedido.setItems(new ArrayList<>(List.of(item)));

        // When
        Pedido guardado = repository.save(pedido);

        // Then
        assertNotNull(guardado.getId());
        assertTrue(guardado.getId() > 0);
        assertEquals(101L, guardado.getClienteId());
        assertEquals(35000.0, guardado.getTotal());
        
        // Verificamos el comportamiento del @PrePersist (onCreate)
        assertEquals("PENDIENTE", guardado.getEstado());
        assertNotNull(guardado.getFechaPedido());
        
        // Verificamos que se haya guardado el item asociado en cascada
        assertNotNull(guardado.getItems());
        assertEquals(1, guardado.getItems().size());
        assertNotNull(guardado.getItems().get(0).getId());
    }

    @Test
    @DisplayName("findAll - debe retornar todos los pedidos guardados en la BD")
    void debeRetornarTodosLosPedidosGuardados() {
        // Given
        Pedido p1 = new Pedido(null, 101L, 15000.0, "PENDIENTE", null, new ArrayList<>());
        Pedido p2 = new Pedido(null, 102L, 50000.0, "PROCESANDO", null, new ArrayList<>());
        repository.save(p1);
        repository.save(p2);

        // When
        List<Pedido> pedidos = repository.findAll();

        // Then
        assertNotNull(pedidos);
        assertEquals(2, pedidos.size());
    }

    @Test
    @DisplayName("findById - debe retornar el pedido correcto cuando el ID existe")
    void debeEncontrarPedidoPorIdExistente() {
        // Given
        Pedido pedido = new Pedido(null, 202L, 89990.0, "ENVIADO", null, new ArrayList<>());
        Pedido guardado = repository.save(pedido);

        // When
        Optional<Pedido> resultado = repository.findById(guardado.getId());

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(202L, resultado.get().getClienteId());
        assertEquals(89990.0, resultado.get().getTotal());
        assertEquals("ENVIADO", resultado.get().getEstado());
    }

    @Test
    @DisplayName("findById - debe retornar Optional vacío cuando el ID no existe")
    void debeRetornarOptionalVacioCuandoIdNoExiste() {
        // When
        Optional<Pedido> resultado = repository.findById(999L);

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("deleteById - debe eliminar el pedido de la base de datos")
    void debeEliminarPedidoPorId() {
        // Given
        Pedido pedido = new Pedido(null, 303L, 12500.0, "PENDIENTE", null, new ArrayList<>());
        Pedido guardado = repository.save(pedido);
        Long id = guardado.getId();

        // When
        repository.deleteById(id);

        // Then
        assertFalse(repository.findById(id).isPresent());
    }
}