package o.horbenko.fsm;

import o.horbenko.fsm.pipeline.FsmPipeline;

/**
 * Finite State Machine (FSM) implementation with Pipeline execution support.
 *
 * @param <S> - State
 * @param <T> - Trigger (Event)
 * @param <D> - Data type
 */
public interface FiniteStateMachine<S, T, D extends FsmStateHolder<S>> extends CoreFsm<S, T, D> {

    /**
     * StateMachine tries to catch all exceptions in transition action, but will throw exception
     * if no exception logic found or on transition to exception state.
     * Continues execution in case, when current state is defined in {@link FsmPipeline}
     * <p>
     * <b>WARNING!</b>
     * Can cause infinite loop processing in case of wrong {@link FiniteStateMachine} or {@link FsmPipeline}
     * configurations (configured FSM not matches finite state machine rules)
     *
     * @param initialDataToProcess context of current state (initial state)
     * @param pipelineToMoveOn     pipeline configuration
     * @return context that contains execution result and updated state.
     * @throws RuntimeException if <ul>
     *                          <li>1. FSM not configured properly (exists loop without exit-point state)</li>
     *                          <li>2. {@link FsmPipeline} not configured properly
     *                          (exists loop without exit-point state)</li>
     *                          </ul>
     */
    D moveOnPipeline(D initialDataToProcess, FsmPipeline<S, T> pipelineToMoveOn);

}
