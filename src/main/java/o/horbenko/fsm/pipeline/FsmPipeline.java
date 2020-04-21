package o.horbenko.fsm.pipeline;

import lombok.NonNull;
import o.horbenko.fsm.error.PipelineConfigurationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Finite State Machine (FSM) pipeline.
 * <p>
 * For pipeline:
 * +-----------------------------------------------------------------------------------+
 * |                                                                                   |
 * |   +--state--+  (by trigger t1)  +--state--+   (by trigger t2)   +--state--+       |
 * |   |    A    |  from--------to   |    B    |   from--------to    |    C    |       |
 * |   +---------+                   +---------+                     +---------+       |
 * |                                                                                   |
 * +-----------------------------------------------------------------------------------+
 * <p>
 * {@link FsmPipeline} would be like:
 * <code>
 * enum States {
 *  A, B, C;
 * }
 * enum Triggers {
 *  T1, T2
 * }
 * new FsmPipeline {@literal <}States, Triggers{@literal >}()
 * .addPipe(States.A, Triggers.T1)
 * .addPipe(States.B, Triggers.T2);
 * </code>
 *
 * @param <S> - State
 * @param <T> - Trigger (Event)
 */
public class FsmPipeline<S, T> {

    private Map<S, T> triggerByStateMap;

    public FsmPipeline() {
        this.triggerByStateMap = new HashMap<>();
    }

    public FsmPipeline(@NonNull Map<S, T> triggerByStateMap) {
        this.triggerByStateMap = triggerByStateMap;
    }

    public Optional<T> getTriggerForNextMovementByState(@NonNull S currentState) {
        return Optional.ofNullable(triggerByStateMap.get(currentState));
    }

    public FsmPipeline<S, T> addPipe(S stateFrom, T triggerForNextMovement) {
        throwIfInvalid(stateFrom, triggerForNextMovement);
        this.triggerByStateMap.put(stateFrom, triggerForNextMovement);
        return this;
    }

    /**
     * @throws PipelineConfigurationException if:
     *                                        <ul>
     *                                            <li>State to move from is <code>null</code></li>
     *                                            <li>Trigger for next transition is <code>null</code></li>
     *                                            <li>Transition from state already exists in pipeline</li>
     *                                        </ul>
     */
    private void throwIfInvalid(S stateFrom, T triggerForNextMovement) {

        if (stateFrom == null || triggerForNextMovement == null) {
            throw new PipelineConfigurationException("Invalid transition arguments! StateFrom=" + stateFrom + " triggerForNextMovement=" + triggerForNextMovement);
        }

        if (triggerByStateMap.containsKey(stateFrom)) {
            throw new PipelineConfigurationException("Trigger for next transition from state=" + stateFrom + " already exists in pipeline");
        }
    }
}
