package o.horbenko.fsm;

/**
 * Basic Finite State Machine (FSM) implementation
 * <p>
 * Movement exception handling logic:
 * <ul>
 *     <li>
 *         1. If exception was thrown in movement action or in movement post-action - handle exception
 *         and try to move by exception trigger
 *     </li>
 *     <li>
 *         2. If in exception handling movement was thrown exception
 *         - tries to handle recursively until there is no trigger defined by new exception state.
 *     </li>
 * </ul>
 *
 * @param <S> State
 * @param <T> Trigger (Event)
 * @param <D> Data type
 */
public interface CoreFsm<S, T, D extends FsmStateHolder<S>> {

    /**
     * Transits state that holds <code>initialDataToProcess</code> to the next state by trigger <code>trigger</code>
     *
     * @param trigger              trigger for movement to the next state
     * @param initialDataToProcess data+state holder
     * @return data+state after movement
     */
    D move(T trigger, D initialDataToProcess);

}
