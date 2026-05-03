package cl.duoc.clientes.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.repository.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> obtenerTodos(){
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardarCliente(Cliente cliente){
        // Aquí podrías validar si el RUT ya existe para lanzar una excepción personalizada
        return clienteRepository.save(cliente);
    }

    // --- REQUERIMIENTO IE 1.2.3: OPERACIÓN UPDATE ---
    public Optional<Cliente> actualizarCliente(Long id, Cliente clienteDetalles) {
        return clienteRepository.findById(id).map(clienteExistente -> {
            clienteExistente.setNombre(clienteDetalles.getNombre());
            clienteExistente.setApellidoPaterno(clienteDetalles.getApellidoPaterno());
            clienteExistente.setCorreo(clienteDetalles.getCorreo());
            clienteExistente.setTelefono(clienteDetalles.getTelefono());
            clienteExistente.setDireccion(clienteDetalles.getDireccion());
            clienteExistente.setComuna(clienteDetalles.getComuna());
            // No actualizamos el RUT ni la fecha de registro por seguridad
            return clienteRepository.save(clienteExistente);
        });
    }

    // --- MEJORA SEMÁNTICA: ELIMINACIÓN POR ID (RECOMENDACIÓN IE 1.1.2) ---
    public boolean eliminarPorId(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean eliminarPorRut(String rut){
        if (clienteRepository.existsByRutIgnoreCase(rut)){
            clienteRepository.deleteByRutIgnoreCase(rut);
            return true;
        }
        return false;
    }
}