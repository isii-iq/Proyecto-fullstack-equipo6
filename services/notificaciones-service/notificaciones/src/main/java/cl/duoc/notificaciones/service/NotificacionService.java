package cl.duoc.notificaciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.notificaciones.client.ClienteClient;
import cl.duoc.notificaciones.client.PedidoClient;
import cl.duoc.notificaciones.dto.ClienteDTO;
import cl.duoc.notificaciones.dto.NotificacionCreateDTO;
import cl.duoc.notificaciones.dto.NotificacionDTO;
import cl.duoc.notificaciones.dto.PedidoDTO;
import cl.duoc.notificaciones.excepciones.RecursoNoEncontradoException;
import cl.duoc.notificaciones.excepciones.ServicioNoDisponibleException;
import cl.duoc.notificaciones.model.Notificacion;
import cl.duoc.notificaciones.repository.NotificacionRepository;
import feign.FeignException;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired
    private NotificacionRepository repository;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private PedidoClient pedidoClient;

    public NotificacionDTO generarNotificacionPedido(Long pedidoId) {
        try {
            log.info("Iniciando flujo de notificación para Pedido ID: {}", pedidoId);

            PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(pedidoId);
            ClienteDTO cliente = clienteClient.getClienteById(pedido.getClienteId());

            Notificacion notificacion = new Notificacion();
            notificacion.setUsuarioId(pedido.getClienteId());
            notificacion.setPedidoId(pedidoId);
            notificacion.setTitulo("Confirmación de Pedido #" + pedidoId);
            
            String mensaje = String.format("Hola %s, tu pedido por $%s ha sido recibido con estado: %s.", 
                             cliente.getNombre(), pedido.getTotal(), pedido.getEstado());
            
            notificacion.setMensaje(mensaje);
            notificacion.setLeido(false);

            Notificacion guardada = repository.save(notificacion);
            return toDTO(guardada);

        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontró información del Pedido o Cliente.");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("Error de comunicación entre microservicios.");
        }
    }

    public List<NotificacionDTO> obtenerTodas() {
        log.info("Consultando todas las notificaciones registradas");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NotificacionDTO obtenerPorId(Long id) {
        log.info("Buscando notificación por ID: {}", id);
        Notificacion notificacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));
        return toDTO(notificacion);
    }

    public NotificacionDTO guardarNotificacion(NotificacionCreateDTO dto) {
        log.info("Iniciando validación para el Usuario (Cliente): {}", dto.getUsuarioId());

        try {
            log.info("Llamando a ms-clientes para validar usuario ID: {}", dto.getUsuarioId());
            clienteClient.getClienteById(dto.getUsuarioId());
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El usuario con ID " + dto.getUsuarioId() + " no existe. No se puede enviar la notificación.");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("Microservicio de Clientes no disponible.");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(dto.getUsuarioId());
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setLeido(false);

        Notificacion guardada = repository.save(notificacion);
        log.info("Notificación creada con éxito para el usuario {}", dto.getUsuarioId());

        return toDTO(guardada);
    }

    @Transactional
    public NotificacionDTO actualizarDesdeJson(Long id, NotificacionCreateDTO dto) {
        Notificacion existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));

        try {
            clienteClient.getClienteById(dto.getUsuarioId());
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El usuario con ID " + dto.getUsuarioId() + " no existe.");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("Microservicio de Clientes no disponible.");
        }

        existente.setUsuarioId(dto.getUsuarioId());
        existente.setTitulo(dto.getTitulo());
        existente.setMensaje(dto.getMensaje());
        
        Notificacion guardada = repository.save(existente);
        return toDTO(guardada);
    }

    public boolean eliminarPorId(Long id) {
        log.info("Eliminando notificación ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean eliminarPorUsuarioId(Long usuarioId) {
        log.info("Eliminando todas las notificaciones del usuario: {}", usuarioId);
        List<Notificacion> lista = repository.findByUsuarioId(usuarioId);
        if (!lista.isEmpty()) {
            repository.deleteAll(lista);
            return true;
        }
        return false;
    }

    private NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(
                n.getId(),
                n.getUsuarioId(),
                n.getTitulo(),
                n.getMensaje(),
                n.isLeido(),
                n.getFechaCreacion()
        );
    }
}