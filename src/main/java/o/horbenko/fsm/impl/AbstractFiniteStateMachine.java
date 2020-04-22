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
package o.horbenko.fsm.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import o.horbenko.fsm.FiniteStateMachine;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.pipeline.FsmPipeline;
import o.horbenko.fsm.state.FsmState;

import java.util.Map;
import java.util.Optional;

/**
 * @see o.horbenko.fsm.FiniteStateMachine interface for details.
 * Extends {@link AbstractCoreFsm} with basic {@link o.horbenko.fsm.CoreFsm#move(Object, FsmStateHolder)} functionality.
 * @param <S> State type
 * @param <T> Trigger (event) type
 * @param <D>
 */
@Slf4j
public class AbstractFiniteStateMachine
        <S, T, D extends FsmStateHolder<S>>
        extends AbstractCoreFsm<S, T, D>
        implements FiniteStateMachine<S, T, D> {

    public AbstractFiniteStateMachine() {
        super();
    }

    public AbstractFiniteStateMachine(Map<S, FsmState<S, T, D>> stateConfigurationMap) {
        super(stateConfigurationMap);
    }

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
    @Override
    public D moveOnPipeline(@NonNull D initialDataToProcess,
                            @NonNull FsmPipeline<S, T> pipelineToMoveOn) {
        log.debug("Trying to move from state {} on pipeline {}", initialDataToProcess, pipelineToMoveOn);

        D result = initialDataToProcess;

        /*
         * Terminates in case, when pipeline does not contains trigger for next movement by current state
         * */
        while (true) {

            Optional<T> movementTriggerOpt = pipelineToMoveOn
                    .getTriggerForNextMovementByState(result.getState());
            log.debug("Found new movement trigger '{}' for movement on pipeline.", movementTriggerOpt);

            if (movementTriggerOpt.isEmpty()) { // exit point
                log.debug("There is no next movement defined in pipeline. Result state is '{}'", result.getState());
                return result;
            } else {
                result = move(movementTriggerOpt.get(), result);
            }
        }
    }

}
