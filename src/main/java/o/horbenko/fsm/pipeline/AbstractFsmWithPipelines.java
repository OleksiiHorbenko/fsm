package o.horbenko.fsm.pipeline;

import lombok.NonNull;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.FsmWithPipelines;
import o.horbenko.fsm.basic.AbstractFsm;
import o.horbenko.fsm.error.NoPipelineFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AbstractFsmWithPipelines
        <S, T, D extends FsmStateHolder<S>, P>
        extends AbstractFsm<S, T, D>
        implements FsmWithPipelines<S, T, D, P> {

    private Map<P, FsmPipeline<S, T>> pipelineByPipelineIdMap;

    public AbstractFsmWithPipelines() {
        this.pipelineByPipelineIdMap = new HashMap<>();
    }


    @Override
    public D moveOnPipeline(@NonNull T trigger,
                            @NonNull D initialDataToProcess,
                            @NonNull P pipelineId) {

        if (!pipelineByPipelineIdMap.containsKey(pipelineId))
            throw new NoPipelineFoundException(pipelineId.toString());

        FsmPipeline<S, T> pipeline = pipelineByPipelineIdMap.get(pipelineId);
        return moveOnPipeline(trigger, initialDataToProcess, pipeline);
    }

    @Override
    public D moveOnPipeline(@NonNull T trigger,
                            @NonNull D initialDataToProcess,
                            @NonNull FsmPipeline<S, T> pipelineToMoveOn) {

        D result = initialDataToProcess;
        T movementTrigger = trigger;

        while (true) {
            result = move(movementTrigger, result);
            Optional<T> movementTriggerOpt = pipelineToMoveOn.getTriggerForNextMovementByState(result.getState());

            if (movementTriggerOpt.isEmpty())
                return result;

            movementTrigger = movementTriggerOpt.get();
        }

    }


    @Override
    public D move(@NonNull T trigger,
                  @NonNull D stateHolder) {
        return super.move(trigger, stateHolder);
    }
}
