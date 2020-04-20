package o.horbenko.fsm;

/**
 * Basic Finite State Machine (FSM) implementation
 *
 * @param <S> - State
 * @param <T> - Trigger (Event)
 * @param <D> - Data type
 */
public interface Fsm<S, T, D extends FsmStateHolder<S>> {

    /**
     * Transits state that holds <code>initialDataToProcess</code> to the next state by trigger <code>trigger</code>
     *
     * @param trigger              trigger for movement to the next state
     * @param initialDataToProcess data+state holder
     * @return data+state after movement
     */
    D move(T trigger, D initialDataToProcess);

}
