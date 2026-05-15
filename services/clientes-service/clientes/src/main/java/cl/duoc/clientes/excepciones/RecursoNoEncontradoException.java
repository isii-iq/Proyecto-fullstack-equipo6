package cl.duoc.clientes.excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super("Cliente no encontrado");
    }
}


