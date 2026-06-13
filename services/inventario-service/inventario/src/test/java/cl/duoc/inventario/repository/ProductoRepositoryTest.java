
package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.Producto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    @DisplayName("save - debe persistir el producto y asignar un ID generado automáticamente")
    void debePersistirProductoYAsignarIdGenerado() {
        // Given -
        Producto producto = new Producto(null, "Teclado Mecánico Redragon", 
            "Accesorios", 15, "RED-44332-RGB", new BigDecimal("59990.00"));

        // When
        Producto guardado = repository.save(producto);

        // Then
        assertNotNull(guardado.getId());
        assertTrue(guardado.getId() > 0);
        assertEquals("Teclado Mecánico Redragon", guardado.getNombre());
        assertEquals(15, guardado.getCantidad());
        assertEquals("RED-44332-RGB", guardado.getSku());
    }

    @Test
    @DisplayName("findAll - debe retornar todos los productos guardados en la BD")
    void debeRetornarTodosLosProductosGuardados() {
        // Given
        repository.save(new Producto(null, "Monitor Samsung 24\"", "Monitores", 8, "SAM-24-HD", new BigDecimal("199990.00")));
        repository.save(new Producto(null, "Webcam Logitech C920", "Accesorios", 20, "LOG-C920-WC", new BigDecimal("79990.00")));

        // When
        List<Producto> productos = repository.findAll();

        // Then
        assertNotNull(productos);
        assertEquals(2, productos.size());
    }

    @Test
    @DisplayName("findById - debe retornar el producto correcto cuando el ID existe")
    void debeEncontrarProductoPorIdExistente() {
        // Given
        Producto guardado = repository.save(
            new Producto(null, "Audífonos Sony WH-1000XM5", "Audio", 12, "SNY-XM5-ANC", new BigDecimal("289990.00"))
        );

        // When
        Optional<Producto> resultado = repository.findById(guardado.getId());

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Audífonos Sony WH-1000XM5", resultado.get().getNombre());
        assertEquals(new BigDecimal("289990.00"), resultado.get().getPrecio());
    }

    @Test
    @DisplayName("deleteById - debe eliminar el producto de la base de datos")
    void debeEliminarProductoPorId() {
        // Given
        Producto guardado = repository.save(
            new Producto(null, "Laptop HP Pavilion", "Computación", 5, "HP-PAV-14", new BigDecimal("499990.00"))
        );
        Long id = guardado.getId();

        // When
        repository.deleteById(id);

        // Then
        assertFalse(repository.findById(id).isPresent());
    }

    // ── TESTS EXTRAS: TUS MÉTODOS PERSONALIZADOS DE SKU ───────────────────────

    @Test
    @DisplayName("existsBySkuIgnoreCase - debe retornar true si el SKU existe sin importar mayúsculas/minúsculas")
    void debeRetornarTrueSiExisteSkuIgnoreCase() {
        // Given
        repository.save(new Producto(null, "Mouse Razer", "Accesorios", 10, "RZR-DEATH-V2", new BigDecimal("45000.00")));

        // When & Then (Probamos mandándolo en minúsculas "rzr-death-v2")
        boolean existe = repository.existsBySkuIgnoreCase("rzr-death-v2");
        
        assertTrue(existe);
    }

    @Test
    @DisplayName("findBySkuIgnoreCase - debe retornar el producto correcto por su SKU")
    void debeEncontrarProductoPorSkuIgnoreCase() {
        // Given
        repository.save(new Producto(null, "Mouse Razer", "Accesorios", 10, "RZR-DEATH-V2", new BigDecimal("45000.00")));

        // When
        Optional<Producto> resultado = repository.findBySkuIgnoreCase("RZR-DEATH-V2");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Mouse Razer", resultado.get().getNombre());
    }

    @Test
    @DisplayName("deleteBySkuIgnoreCase - debe borrar el registro usando el SKU")
    void debeEliminarProductoUsandoSkuIgnoreCase() {
        // Given
        repository.save(new Producto(null, "Mouse Razer", "Accesorios", 10, "RZR-DEATH-V2", new BigDecimal("45000.00")));

        // When
        repository.deleteBySkuIgnoreCase("rzr-death-v2");
        repository.flush(); 

        // Then
        boolean existe = repository.existsBySkuIgnoreCase("RZR-DEATH-V2");
        assertFalse(existe);
    }
}