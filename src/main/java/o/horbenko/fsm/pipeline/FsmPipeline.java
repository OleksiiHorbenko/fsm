/*
 * MIT License
 *
 * Copyright (c) 2020 Oleksii Horbenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * */
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
 * <br> A --(by trigger t1)--{@literal >} B --(by trigger t2)--{@literal >} C
 * <p>
 * {@link FsmPipeline} would be like:
 * <code>
 * <br>enum States {
 * <br>&nbsp;A, B, C;
 * <br>}
 * <br>enum Triggers {
 * <br>&nbsp;T1, T2
 * <br>}
 * <br>new FsmPipeline {@literal <}States, Triggers{@literal >}()
 * <br>&nbsp;.addPipe(States.A, Triggers.T1)
 * <br>&nbsp;.addPipe(States.B, Triggers.T2);
 * </code>
 *
 * @param <S> - State
 * @param <T> - Trigger (Event)
 * @author Oleksii Horbenko
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

    /**
     * Adds new movement in pipeline. (Specifies new transition from <code>stateFrom</code> to new state by trigger <code>triggerForNextMovement</code>)
     *
     * @param stateFrom              state to move from
     * @param triggerForNextMovement trigger that specifies the next {@link o.horbenko.fsm.movement.FsmMovement} (contains target state)
     * @return {@link FsmPipeline} as implementation of builder
     * @throws PipelineConfigurationException if one of input params is null or if transition from <code>stateFrom</code>  alrady exists.
     */
    public FsmPipeline<S, T> addPipe(S stateFrom, T triggerForNextMovement) {
        throwIfInvalid(stateFrom, triggerForNextMovement);
        this.triggerByStateMap.put(stateFrom, triggerForNextMovement);
        return this;
    }

    /**
     * @throws PipelineConfigurationException <ul>
     *                                        <li>State to move from is <code>null</code></li>
     *                                        <li>Trigger for next transition is <code>null</code></li>
     *                                        <li>Transition from state already exists in pipeline</li>
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
