package cl.duoc.pagos.excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super("Recurso no encontrado");
    }
}


