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
package o.horbenko.fsm.movement;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.movementaction.FsmMovementAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * StateMachine transition configuration.
 * <p>
 * {@link FsmMovement#movementAction} - main action to be applied on state machine.
 * State of domain is initial (initial state before transition)
 * {@link FsmMovement#postMovementAction} - action after successful {@link #movementAction}.
 * State of domain is {@link FsmMovement#futureStateOnSuccess}. Can be used for storing new state in DB.
 *
 * @param <S> state type
 * @param <T> trigger (event) type
 * @param <D> state holder or context
 * @author Oleksii Horbenko
 */
@Data
@Accessors(chain = true)
public class FsmMovement<S, T, D extends FsmStateHolder<S>> {

    private FsmMovementAction<D> movementAction;
    private FsmMovementAction<D> postMovementAction;

    private S futureStateOnSuccess;

    // exceptions handling
    private Map<Class<? extends Exception>, T> triggerByException;
    private T triggerOnGeneralException;


    public FsmMovement() {
        this.triggerByException = new HashMap<>();
    }

    public FsmMovement<S, T, D> targetState(@NonNull S targetState) {
        this.futureStateOnSuccess = targetState;
        return this;
    }

    public FsmMovement<S, T, D> movementAction(@NonNull FsmMovementAction<D> action) {
        this.movementAction = action;
        return this;
    }

    public FsmMovement<S, T, D> postMovementAction(@NonNull FsmMovementAction<D> action) {
        this.postMovementAction = action;
        return this;
    }

    public FsmMovement<S, T, D> triggerOnGeneralException(@NonNull T trigger) {
        this.triggerOnGeneralException = trigger;
        return this;
    }

    public FsmMovement<S, T, D> triggerOnException(@NonNull Class<? extends Exception> exceptionType,
                                                   @NonNull T triggerOnException) {
        this.triggerByException.put(exceptionType, triggerOnException);
        return this;
    }

    public Optional<T> getTriggerByConcreteException(Class<? extends Exception> exceptionType) {
        return Optional.ofNullable(triggerByException.get(exceptionType));
    }


    public Optional<FsmMovementAction<D>> getMovementAction() {
        return Optional.ofNullable(movementAction);
    }

    public Optional<FsmMovementAction<D>> getPostMovementAction() {
        return Optional.ofNullable(postMovementAction);
    }


}
