package cl.duoc.clientes.service;

import cl.duoc.clientes.dto.ClienteCreateDTO;
import cl.duoc.clientes.dto.ClienteDTO;
import cl.duoc.clientes.excepciones.RecursoNoEncontradoException;
import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository repository;

    public List<ClienteDTO> obtenerTodos() {
        log.info("Consultando todos los clientes");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO obtenerPorId(Long id) {
        log.info("Buscando cliente id={}", id);
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
        return toDTO(cliente);
    }

    public ClienteDTO guardarCliente(ClienteCreateDTO dto) {
        log.info("Creando nuevo cliente con RUT: {}", dto.getRut());
        
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellidoPaterno(dto.getApellidoPaterno());
        cliente.setRut(dto.getRut());
        cliente.setCorreo(dto.getCorreo());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setComuna(dto.getComuna());

        Cliente guardado = repository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {} y RUT: {}", guardado.getId(), guardado.getRut());
        
        return toDTO(guardado);
    }

    public ClienteDTO actualizarCliente(Long id, ClienteCreateDTO dto) {
        log.info("Actualizando cliente id={}", id);
        
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente para actualizar: " + id));

        existente.setNombre(dto.getNombre());
        existente.setApellidoPaterno(dto.getApellidoPaterno());
        existente.setRut(dto.getRut());
        existente.setCorreo(dto.getCorreo());
        existente.setTelefono(dto.getTelefono());
        existente.setDireccion(dto.getDireccion());
        existente.setComuna(dto.getComuna());

        Cliente actualizado = repository.save(existente);
        log.info("Cliente id={} actualizado correctamente", actualizado.getId());
        
        return toDTO(actualizado);
    }

    public boolean eliminarPorId(Long id) {
        log.info("Intentando eliminar cliente id={}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Cliente id={} eliminado", id);
            return true;
        }
        log.warn("No se pudo eliminar: Cliente id={} no existe", id);
        return false;
    }

    public boolean eliminarPorRut(String rut) {
        log.info("Intentando eliminar cliente con RUT={}", rut);
        if (repository.existsByRutIgnoreCase(rut)) {
            repository.deleteByRutIgnoreCase(rut);
            log.info("Cliente con RUT={} eliminado", rut);
            return true;
        }
        log.warn("No se pudo eliminar: RUT={} no encontrado", rut);
        return false;
    }

    private ClienteDTO toDTO(Cliente c) {
        return new ClienteDTO(
                c.getId(),
                c.getNombre(),
                c.getApellidoPaterno(),
                c.getRut(),
                c.getCorreo(),
                c.getTelefono(),
                c.getDireccion(),
                c.getComuna()
        );
    }
}