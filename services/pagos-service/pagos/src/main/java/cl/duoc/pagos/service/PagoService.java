package cl.duoc.pagos.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagosRepository;

@Service
public class PagoService {
    private final PagosRepository pagoRepository;

    public PagoService(PagosRepository pagoRepository){
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> obtenerTodos(){
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public Pago registrarPago(Pago pago){
    
        if(pagoRepository.existsByNumeroBoletaIgnoreCase(pago.getNumeroBoleta())){
            throw new RuntimeException("El número de boleta ya está registrado");
        }
        
        if(pago.getEstado() == null) {
            pago.setEstado("PENDIENTE");
        }
        return pagoRepository.save(pago);
    }

    public Optional<Pago> actualizarEstado(Long id, String nuevoEstado) {
        return pagoRepository.findById(id).map(pagoExistente -> {
            pagoExistente.setEstado(nuevoEstado);
            return pagoRepository.save(pagoExistente);
        });
    }

    public boolean eliminarPorBoleta(String numeroBoleta){
        if (pagoRepository.existsByNumeroBoletaIgnoreCase(numeroBoleta)){
            pagoRepository.deleteByNumeroBoletaIgnoreCase(numeroBoleta);
            return true;
        }
        return false;
    }



    public Optional<Pago> actualizarPago(Long id, Pago pagoDetalles) {
        return pagoRepository.findById(id).map(pagoExistente -> {
            pagoExistente.setMonto(pagoDetalles.getMonto());
            pagoExistente.setMetodoPago(pagoDetalles.getMetodoPago());
            pagoExistente.setEstado(pagoDetalles.getEstado());
            return pagoRepository.save(pagoExistente);
        });
    }
}