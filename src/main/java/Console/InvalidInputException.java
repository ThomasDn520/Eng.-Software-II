package Console;

/**
 * Representa uma exceção que ocorre quando a entrada não é válida
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Construtor
     * @param message Descrição de como a exceção ocorreu
     */
    public InvalidInputException(String message) {
        super(message);
    }
}