package cl.duoc.catalogo.repository;

import cl.duoc.catalogo.model.ProductoCatalogo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoCatalogoRepositoryTest {

    @Autowired
    private ProductoCatalogoRepository repository;

    @Test
    @DisplayName("save - debe persistir el producto y asignar un ID generado automáticamente")
    void debePersistirProductoYAsignarIdGenerado() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setNombre("Teclado Mecánico Redragon");
        producto.setDescripcion("Teclado RGB switches Blue");
        producto.setPrecio(59990.0);
        producto.setCategoria("Perifericos");
        producto.setSku("TEC-RED");
        producto.setDisponible(true);

        // When
        ProductoCatalogo guardado = repository.save(producto);

        // Then
        assertNotNull(guardado.getId());
        assertTrue(guardado.getId() > 0);
        assertEquals("Teclado Mecánico Redragon", guardado.getNombre());
        assertEquals("TEC-RED", guardado.getSku());
    }

    @Test
    @DisplayName("findAll - debe retornar todos los productos guardados en la BD")
    void debeRetornarTodosLosProductosGuardados() {
        // Given
        ProductoCatalogo p1 = new ProductoCatalogo();
        p1.setNombre("Monitor Samsung 24\"");
        p1.setPrecio(199990.0);
        p1.setCategoria("Monitores");
        p1.setSku("SAM-24");
        repository.save(p1);

        ProductoCatalogo p2 = new ProductoCatalogo();
        p2.setNombre("Webcam Logitech C920");
        p2.setPrecio(79990.0);
        p2.setCategoria("Accesorios");
        p2.setSku("LOG-C920");
        repository.save(p2);

        // When
        List<ProductoCatalogo> productos = repository.findAll();

        // Then
        assertNotNull(productos);
        assertEquals(2, productos.size());
    }

    @Test
    @DisplayName("findById - debe retornar el producto correcto cuando el ID existe")
    void debeEncontrarProductoPorIdExistente() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setNombre("Audífonos Sony WH-1000XM5");
        producto.setPrecio(289990.0);
        producto.setCategoria("Audio");
        producto.setSku("SONY-XM5");
        ProductoCatalogo guardado = repository.save(producto);

        // When
        Optional<ProductoCatalogo> resultado = repository.findById(guardado.getId());

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Audífonos Sony WH-1000XM5", resultado.get().getNombre());
    }

    @Test
    @DisplayName("findById - debe retornar Optional vacío cuando el ID no existe")
    void debeRetornarOptionalVacioCuandoIdNoExiste() {
        // When
        Optional<ProductoCatalogo> resultado = repository.findById(999L);

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("deleteById - debe eliminar el producto de la base de datos")
    void debeEliminarProductoPorId() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setNombre("Laptop HP Pavilion");
        producto.setPrecio(499990.0);
        producto.setCategoria("Computadores");
        producto.setSku("HP-PAV");
        ProductoCatalogo guardado = repository.save(producto);
        Long id = guardado.getId();

        // When
        repository.deleteById(id);

        // Then
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    @DisplayName("findBySkuIgnoreCase - debe encontrar producto independiente de mayúsculas/minúsculas")
    void testFindBySkuIgnoreCase() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setNombre("Monitor Gamer");
        producto.setPrecio(180000.0);
        producto.setCategoria("Monitores");
        producto.setSku("MON-144");
        repository.save(producto);

        // When
        Optional<ProductoCatalogo> encontradoMinuscula = repository.findBySkuIgnoreCase("mon-144");
        Optional<ProductoCatalogo> encontradoMayuscula = repository.findBySkuIgnoreCase("MON-144");

        // Then
        assertTrue(encontradoMinuscula.isPresent());
        assertTrue(encontradoMayuscula.isPresent());
        assertEquals("Monitor Gamer", encontradoMinuscula.get().getNombre());
    }

    @Test
    @DisplayName("existsBySkuIgnoreCase - debe retornar verdadero si el SKU ya existe")
    void testExistsBySkuIgnoreCase() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setNombre("Monitor Gamer");
        producto.setPrecio(180000.0);
        producto.setCategoria("Monitores");
        producto.setSku("MON-144");
        repository.save(producto);

        // When
        boolean existe = repository.existsBySkuIgnoreCase("mon-144");
        boolean noExiste = repository.existsBySkuIgnoreCase("SINKODIGO");

        // Then
        assertTrue(existe);
        assertFalse(noExiste);
    }

    @Test
    @DisplayName("findByCategoriaIgnoreCase - debe retornar la lista de productos que pertenecen a la categoría")
    void testFindByCategoriaIgnoreCase() {
        // Given
        ProductoCatalogo producto = new ProductoCatalogo();
        producto.setNombre("Monitor Gamer");
        producto.setPrecio(180000.0);
        producto.setCategoria("Monitores");
        producto.setSku("MON-144");
        repository.save(producto);

        // When
        List<ProductoCatalogo> resultado = repository.findByCategoriaIgnoreCase("monitores");

        // Then
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Monitores", resultado.get(0).getCategoria());
    }

    @Test
    @DisplayName("findByDisponibleTrue - debe retornar solo los productos marcados como disponibles")
    void testFindByDisponibleTrue() {
        // Given
        ProductoCatalogo productoDisponible = new ProductoCatalogo();
        productoDisponible.setNombre("Mouse Activo");
        productoDisponible.setSku("MOU-ACT");
        productoDisponible.setPrecio(10000.0);
        productoDisponible.setCategoria("Perifericos");
        productoDisponible.setDisponible(true);
        repository.save(productoDisponible);

        ProductoCatalogo productoOculto = new ProductoCatalogo();
        productoOculto.setNombre("Mouse Viejo");
        productoOculto.setSku("MOU-OLD");
        productoOculto.setPrecio(5000.0);
        productoOculto.setCategoria("Perifericos");
        productoOculto.setDisponible(false);
        repository.save(productoOculto);

        // When
        List<ProductoCatalogo> disponibles = repository.findByDisponibleTrue();

        // Then
        assertFalse(disponibles.isEmpty());
        assertTrue(disponibles.stream().allMatch(ProductoCatalogo::isDisponible));
    }
}