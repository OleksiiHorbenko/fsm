package o.horbenko.fsm.error;

public class NoMovementByTriggerInStateException extends RuntimeException{

    public NoMovementByTriggerInStateException(String s) {
        super(s);
    }
}
