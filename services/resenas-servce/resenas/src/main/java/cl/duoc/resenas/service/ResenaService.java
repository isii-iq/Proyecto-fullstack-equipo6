package cl.duoc.resenas.service;

import cl.duoc.resenas.client.CatalogoClient;
import cl.duoc.resenas.client.ClienteClient;
import cl.duoc.resenas.dto.CatalogoDTO;
import cl.duoc.resenas.dto.ResenaCreateDTO;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.excepciones.RecursoNoEncontradoException;
import cl.duoc.resenas.excepciones.ServicioNoDisponibleException;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.repository.ResenaRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaService.class);

    @Autowired
    private ResenaRepository repository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Autowired
    private ClienteClient clienteClient;

    public List<ResenaDTO> obtenerTodos() {
        log.info("Consultando todas las reseñas registradas");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ResenaDTO obtenerPorId(Long id) {
        log.info("Buscando reseña por ID: {}", id);
        Resena resena = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reseña no encontrada con ID: " + id));
        return toDTO(resena);
    }
   
    public ResenaDTO actualizar(Long id, ResenaCreateDTO dto) {

    Resena existente = repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reseña con ID: " + id));

   
    existente.setComentario(dto.getComentario());
    existente.setCalificacion(dto.getCalificacion());

    Resena actualizado = repository.save(existente);
    return toDTO(actualizado); 
}



    public ResenaDTO guardar(ResenaCreateDTO dto) {
        log.info("Iniciando validaciones para Producto: {} y Cliente: {}", dto.getProductoId(), dto.getClienteId());

        try {
            log.info("Llamando a ms-clientes para validar cliente ID: {}", dto.getClienteId());
            clienteClient.getClienteById(dto.getClienteId());
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El cliente con ID " + dto.getClienteId() + " no existe.");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("Microservicio de Clientes no disponible.");
        }

       
        try {
            log.info("Llamando a ms-catalogo para validar producto ID: {}", dto.getProductoId());
            CatalogoDTO producto = catalogoClient.obtenerProductoPorId(dto.getProductoId());
            
            if (!producto.isDisponible()) {
                throw new RecursoNoEncontradoException("El producto existe pero no está disponible para recibir reseñas.");
            }
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El producto con ID " + dto.getProductoId() + " no existe en el catálogo.");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("Microservicio de Catálogo o Inventario no disponible.");
        }

  
        Resena resena = new Resena();
        resena.setProductoId(dto.getProductoId());
        resena.setClienteId(dto.getClienteId());
        resena.setComentario(dto.getComentario());
        resena.setCalificacion(dto.getCalificacion());

        Resena guardada = repository.save(resena);
        log.info("Reseña creada con éxito para el producto {}", dto.getProductoId());

        return toDTO(guardada);
    }

    public boolean eliminar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private ResenaDTO toDTO(Resena r) {
        return new ResenaDTO(
                r.getId(),
                r.getProductoId(),
                r.getClienteId(),
                r.getComentario(),
                r.getCalificacion(),
                r.getFechaCreacion()
        );
    }
}