package eu.dominiktousek.pv168.cookbook.daocontext;

/**
 *
 * @author Dominik Tousek (422385)
 */
public class ConfigLoadFailureException extends RuntimeException{

    public ConfigLoadFailureException(String message) {
        super(message);
    }

    public ConfigLoadFailureException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
