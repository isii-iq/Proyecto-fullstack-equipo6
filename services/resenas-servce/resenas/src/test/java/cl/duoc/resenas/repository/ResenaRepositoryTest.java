package cl.duoc.resenas.repository;

import cl.duoc.resenas.model.Resena;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ResenaRepositoryTest {

    @Autowired
    private ResenaRepository repository;

    @Test
    @DisplayName("save - debe persistir la reseña y asignar un ID generado automáticamente")
    void debePersistirResenaYAsignarIdGenerado() {
        Resena resena = new Resena();
        resena.setProductoId(10L);
        resena.setClienteId(50L);
        resena.setComentario("Muy buen producto, cumple las expectativas.");
        resena.setCalificacion(5);

        Resena guardada = repository.save(resena);

        assertNotNull(guardada.getId());
        assertTrue(guardada.getId() > 0);
        assertEquals(10L, guardada.getProductoId());
        assertEquals("Muy buen producto, cumple las expectativas.", guardada.getComentario());
    }

    @Test
    @DisplayName("findAll - debe retornar todas las reseñas guardadas en la BD")
    void debeRetornarTodasLasResenasGuardadas() {
        Resena r1 = new Resena();
        r1.setProductoId(10L);
        r1.setClienteId(50L);
        r1.setComentario("Excelente");
        r1.setCalificacion(5);
        repository.save(r1);

        Resena r2 = new Resena();
        r2.setProductoId(11L);
        r2.setClienteId(51L);
        r2.setComentario("Regular");
        r2.setCalificacion(3);
        repository.save(r2);

        List<Resena> resenas = repository.findAll();

        assertNotNull(resenas);
        assertEquals(2, resenas.size());
    }

    @Test
    @DisplayName("findById - debe retornar la reseña correcta cuando el ID existe")
    void debeEncontrarResenaPorIdExistente() {
        Resena resena = new Resena();
        resena.setProductoId(10L);
        resena.setClienteId(50L);
        resena.setComentario("No me gustó mucho.");
        resena.setCalificacion(2);
        Resena guardada = repository.save(resena);

        Optional<Resena> resultado = repository.findById(guardada.getId());

        assertTrue(resultado.isPresent());
        assertEquals("No me gustó mucho.", resultado.get().getComentario());
    }

    @Test
    @DisplayName("findById - debe retornar Optional vacío cuando el ID no existe")
    void debeRetornarOptionalVacioCuandoIdNoExiste() {
        Optional<Resena> resultado = repository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("deleteById - debe eliminar la reseña de la base de datos")
    void debeEliminarResenaPorId() {
        Resena resena = new Resena();
        resena.setProductoId(10L);
        resena.setClienteId(50L);
        resena.setComentario("Para borrar");
        resena.setCalificacion(4);
        Resena guardada = repository.save(resena);
        Long id = guardada.getId();

        repository.deleteById(id);

        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    @DisplayName("findByProductoId - debe retornar la lista de reseñas pertenecientes a un producto específico")
    void testFindByProductoId() {
        Resena r1 = new Resena();
        r1.setProductoId(100L);
        r1.setClienteId(50L);
        r1.setComentario("Primer comentario producto 100");
        r1.setCalificacion(4);
        repository.save(r1);

        Resena r2 = new Resena();
        r2.setProductoId(100L);
        r2.setClienteId(51L);
        r2.setComentario("Segundo comentario producto 100");
        r2.setCalificacion(5);
        repository.save(r2);

        Resena r3 = new Resena();
        r3.setProductoId(200L);
        r3.setClienteId(52L);
        r3.setComentario("Comentario producto 200");
        r3.setCalificacion(1);
        repository.save(r3);

        List<Resena> resultado = repository.findByProductoId(100L);

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> r.getProductoId().equals(100L)));
    }

    @Test
    @DisplayName("findByClienteId - debe retornar la lista de reseñas escritas por un cliente específico")
    void testFindByClienteId() {
        Resena r1 = new Resena();
        r1.setProductoId(10L);
        r1.setClienteId(800L);
        r1.setComentario("Reseña del cliente 800");
        r1.setCalificacion(5);
        repository.save(r1);

        Resena r2 = new Resena();
        r2.setProductoId(20L);
        r2.setClienteId(900L);
        r2.setComentario("Reseña de otro cliente");
        r2.setCalificacion(3);
        repository.save(r2);

        List<Resena> resultado = repository.findByClienteId(800L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(800L, resultado.get(0).getClienteId());
    }
}