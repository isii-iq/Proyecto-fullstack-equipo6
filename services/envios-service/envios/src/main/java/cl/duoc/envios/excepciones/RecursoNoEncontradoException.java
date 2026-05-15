package cl.duoc.envios.excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super("Recurso no encontrado");
    }
}


