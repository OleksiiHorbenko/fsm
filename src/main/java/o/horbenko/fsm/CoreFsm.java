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
 * @author Oleksii Horbenko
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
