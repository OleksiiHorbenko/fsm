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
public interface FsmWithPipelines<S, T, D extends FsmStateHolder<S>, P> extends Fsm<S, T, D> {

    D moveOnPipeline(T trigger, D initialDataToProcess, FsmPipeline<S, T> pipelineToMoveOn);

    D moveOnPipeline(T trigger, D initialDataToProcess, P pipelineId);

}
