package eu.dominiktousek.pv168.cookbook;

/**
 * Exception class signalizing Failure of the service
 * 
 * @author Dominik Tousek (422385)
 */
public class ServiceFailureException extends RuntimeException{

    public ServiceFailureException(String message) {
        super(message);
    }
    
    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
