package cl.duoc.inventario.excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super("Recurso no encontrado");
    }
}


