package eu.dominiktousek.pv168.cookbook;

/**
 * Exception class, which occurs when no suitable entity was found
 * 
 * @author Dominik Tousek (422385)
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
