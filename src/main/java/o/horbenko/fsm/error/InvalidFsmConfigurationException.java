package o.horbenko.fsm.error;

public class InvalidFsmConfigurationException extends RuntimeException {
    public InvalidFsmConfigurationException(String s) {
        super(s);
    }
}
