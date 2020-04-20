package o.horbenko.fsm;

/**
 * DTO that holds state
 */
public interface FsmStateHolder<S> {

    S getState();

    void setState(S newState);

}
