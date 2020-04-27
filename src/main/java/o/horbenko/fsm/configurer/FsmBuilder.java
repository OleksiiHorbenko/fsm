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
 */
package o.horbenko.fsm.configurer;

import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.impl.AbstractFiniteStateMachine;
import o.horbenko.fsm.movement.FsmMovement;
import o.horbenko.fsm.movementaction.FsmMovementAction;
import o.horbenko.fsm.state.FsmState;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder of {@link o.horbenko.fsm.FiniteStateMachine} instance with configuration of states,
 * triggers and possible transitions.
 *
 * @author Oleksii Horbenko
 */
public class FsmBuilder<S, T, D extends FsmStateHolder<S>> {

    private Map<S, FsmState<S, T, D>> stateConfigurationMap;

    public static <S, T, D extends FsmStateHolder<S>>
    FsmBuilder<S, T, D> builder() {
        return new FsmBuilder<>();
    }

    public FsmBuilder() {
        this.stateConfigurationMap = new HashMap<>();
    }

    public StateBuilder<S, T, D> andState(S state) {
        return new StateBuilder<>(state, this);
    }

    protected void addState(S state, Map<T, FsmMovement<S, T, D>> stateConfig) {
        this.stateConfigurationMap.put(state, new FsmState<>(stateConfig));
    }

    public AbstractFiniteStateMachine<S, T, D> buildFsm() {
        return new AbstractFiniteStateMachine<>(stateConfigurationMap);
    }

    public static class StateBuilder<S, T, D extends FsmStateHolder<S>> {

        private S state;
        private Map<T, FsmMovement<S, T, D>> possibleMovements;

        private FsmBuilder<S, T, D> parentBuilder;

        public StateBuilder(S state, FsmBuilder<S, T, D> parentBuilder) {
            this.state = state;
            this.possibleMovements = new HashMap<>();
            this.parentBuilder = parentBuilder;
        }

        public MovementBuilder<S, T, D> andStateMovementBy(T trigger) {
            return new MovementBuilder<>(trigger, this);
        }

        public StateBuilder<S, T, D> andState(S state) {
            this.parentBuilder.addState(this.state, possibleMovements);
            return this.parentBuilder.andState(state);
        }

        public AbstractFiniteStateMachine<S, T, D> buildFsm() {
            this.parentBuilder.addState(this.state, this.possibleMovements);
            return parentBuilder.buildFsm();
        }

        protected void addMovement(T trigger, FsmMovement<S, T, D> movement) {
            this.possibleMovements.put(trigger, movement);
        }
    }

    public static class MovementBuilder<S, T, D extends FsmStateHolder<S>> {

        private T trigger;
        private FsmMovement<S, T, D> movement;
        private StateBuilder<S, T, D> parentBuilder;

        public MovementBuilder(T trigger, StateBuilder<S, T, D> parentBuilder) {
            this.parentBuilder = parentBuilder;
            this.movement = new FsmMovement<>();
            this.trigger = trigger;
        }

        public MovementBuilder<S, T, D> movementAction(FsmMovementAction<D> action) {
            this.movement.movementAction(action);
            return this;
        }

        public MovementBuilder<S, T, D> postMovementAction(FsmMovementAction<D> postMovementAction) {
            this.movement.postMovementAction(postMovementAction);
            return this;
        }

        public MovementBuilder<S, T, D> targetStateOnSuccess(S targetState) {
            this.movement.targetState(targetState);
            return this;
        }

        public MovementBuilder<S, T, D> exceptionTrigger(Class<? extends Exception> exceptionType,
                                                         T triggerOnException) {
            this.movement.triggerOnException(exceptionType, triggerOnException);
            return this;
        }

        public MovementBuilder<S, T, D> exceptionTrigger(T triggerOnGeneralException) {
            this.movement.triggerOnGeneralException(triggerOnGeneralException);
            return this;
        }


        public MovementBuilder<S, T, D> andStateMovementBy(T trigger) {
            this.parentBuilder.addMovement(this.trigger, this.movement);
            return parentBuilder.andStateMovementBy(trigger);
        }


        public StateBuilder<S, T, D> andState(S newState) {
            this.parentBuilder.addMovement(this.trigger, this.movement);
            return this.parentBuilder.andState(newState);
        }


        public AbstractFiniteStateMachine<S, T, D> buildFsm() {
            this.parentBuilder.addMovement(this.trigger, this.movement);
            return this.parentBuilder.buildFsm();
        }

    }

}
