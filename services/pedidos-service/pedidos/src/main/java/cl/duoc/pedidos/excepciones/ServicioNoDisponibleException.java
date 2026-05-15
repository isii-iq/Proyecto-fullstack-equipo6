package cl.duoc.pedidos.excepciones;

public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super("Servicio no disponible");
    }
}

