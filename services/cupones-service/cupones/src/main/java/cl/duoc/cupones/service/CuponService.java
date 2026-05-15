package cl.duoc.cupones.service;

import cl.duoc.cupones.client.PedidoClient;
import cl.duoc.cupones.dto.PedidoDTO;
import cl.duoc.cupones.model.Cupon;
import cl.duoc.cupones.repository.CuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuponService {

    @Autowired
    private CuponRepository repository;

    @Autowired
    private PedidoClient pedidoClient;


    public List<Cupon> listarTodos() {
        return repository.findAll();
    }

    public Cupon buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado con ID: " + id));
    }

    public Cupon crear(Cupon cupon) {
        if (repository.existsByCodigo(cupon.getCodigo())) {
            throw new RuntimeException("El código de cupón ya existe");
        }
        return repository.save(cupon);
    }

    public Cupon actualizar(Long id, Cupon datosNuevos) {
        Cupon existente = buscarPorId(id);
        existente.setCodigo(datosNuevos.getCodigo());
        existente.setPorcentajeDescuento(datosNuevos.getPorcentajeDescuento());
        existente.setUsado(datosNuevos.getUsado());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }


    public Double aplicarDescuentoAPedido(Long idCupon) {
        Cupon cupon = buscarPorId(idCupon);
        
        if (cupon.getUsado()) {
            throw new RuntimeException("Este cupón ya ha sido utilizado");
        }

        PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(cupon.getPedidoId());

        Double montoDescuento = pedido.getTotal() * (cupon.getPorcentajeDescuento() / 100);
        Double nuevoTotal = pedido.getTotal() - montoDescuento;

        cupon.setUsado(true);
        repository.save(cupon);

        return nuevoTotal;
    }
}