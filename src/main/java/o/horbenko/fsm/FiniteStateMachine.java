package o.horbenko.fsm;

import o.horbenko.fsm.pipeline.FsmPipeline;

/**
 * Finite State Machine (FSM) implementation with Pipeline execution support.
 *
 * @param <S> - State
 * @param <T> - Trigger (Event)
 * @param <D> - Data type
 * @param <P> {@link FsmPipeline} ID
 */
public interface FiniteStateMachine<S, T, D extends FsmStateHolder<S>> extends CoreFsm<S, T, D> {

    D moveOnPipeline(D initialDataToProcess, FsmPipeline<S, T> pipelineToMoveOn);

}
