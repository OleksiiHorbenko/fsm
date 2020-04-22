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
