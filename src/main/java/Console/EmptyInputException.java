package Console;

/**
 * Representa uma exceção que ocorre quando a entrada é uma string vazia
 */
public class EmptyInputException extends RuntimeException {

    /**
     * Construtor
     * @param message Descrição de como a exceção ocorreu
     */
    public EmptyInputException(String message) {
        super(message);
    }
}