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
import o.horbenko.fsm.CoreFsm;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.error.InvalidFsmConfigurationException;
import o.horbenko.fsm.error.NoMovementByTriggerInStateException;
import o.horbenko.fsm.movement.FsmMovement;
import o.horbenko.fsm.movementaction.FsmMovementAction;
import o.horbenko.fsm.state.FsmState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Basic implementation of finite state machine with one business method - {@link CoreFsm#move(Object, FsmStateHolder)}
 * <br>Can be configured by {@link o.horbenko.fsm.configurer.FsmBuilder}.
 *
 * @param <S> - state type
 * @param <T> - trigger (event) type
 * @param <D> - object that owns state
 * @author Oleksii Horbenko
 */
@Slf4j
public class AbstractCoreFsm
        <S, T, D extends FsmStateHolder<S>>
        implements CoreFsm<S, T, D> {

    private Map<S, FsmState<S, T, D>> stateConfigurationMap;

    public AbstractCoreFsm() {
        this.stateConfigurationMap = new HashMap<>();
    }

    public AbstractCoreFsm(Map<S, FsmState<S, T, D>> stateConfigurationMap) {
        this.stateConfigurationMap = stateConfigurationMap;
    }

    public AbstractCoreFsm<S, T, D> withState(@NonNull S state,
                                              @NonNull FsmState<S, T, D> stateConfig) {

        if (stateConfigurationMap.containsKey(state)) {
            throw new InvalidFsmConfigurationException("FSM alrady contains configuration for state = " + state.toString());
        }

        this.stateConfigurationMap.put(state, stateConfig);
        return this;
    }

    @Override
    public D move(T trigger, D stateHolder) {
        log.debug("Trying to move from state {} by trigger {}", stateHolder.getState(), trigger);

        // 1. Get current state
        S initialState = stateHolder.getState();

        // 2. Get FsmState configuration
        FsmState<S, T, D> stateConfiguration = stateConfigurationMap.get(initialState);
        log.debug("Found state configuration by state={}", initialState);

        // 3. Get movement by trigger
        FsmMovement<S, T, D> movement = stateConfiguration
                .getMovementByTrigger(trigger)
                .orElseThrow(() -> new NoMovementByTriggerInStateException("Unable to find movement from state = '" + initialState.toString() + "' by trigger = '" + trigger + "'"));
        log.debug("Found movement by trigger={} from state={}", trigger, initialState);

        // 4. Execute
        return moveWithExceptionHandling(movement, stateHolder);
    }


    private D moveWithExceptionHandling(FsmMovement<S, T, D> movement, D data) {
        S initialState = data.getState();
        try {
            log.debug("Trying to execute movement action and postMovement action with exception handling.");

            data = applyActionIfExists(movement.getMovementAction(), data);
            data.setState(movement.getFutureStateOnSuccess());
            log.debug("Main movement action completed successfully. Trying to execute postMovementAction with new state={}", data.getState());
            return applyActionIfExists(movement.getPostMovementAction(), data);

        } catch (Exception e) {
            log.error("Handled error on movement action or post movement action. Trying to change state to initial ({}) and transit to error state.", initialState);
            data.setState(initialState);
            return handleMovementException(movement, data, e);
        }
    }

    private D handleMovementException(FsmMovement<S, T, D> movement,
                                      D data,
                                      Exception e) {
        Optional<T> concreteExceptionTriggerOpt = movement
                .getTriggerByConcreteException(e.getClass());

        T nextMovementTrigger = concreteExceptionTriggerOpt
                .orElseGet(movement::getTriggerOnGeneralException);

        log.debug("Trigger for next movement by handled movement exception is {}. Trying to move by this trigger.", nextMovementTrigger);
        return move(nextMovementTrigger, data);
    }

    private D applyActionIfExists(Optional<FsmMovementAction<D>> actionOpt, D data) {
        if (actionOpt.isPresent()) {
            log.debug("Found action on movement from state={}", data.getState());
            return actionOpt
                    .get().execute(data);
        } else {
            // do nothing
            log.debug("Movement action for state={} not found in configuration. Ignoring.", data.getState());
            return data;
        }
    }

}
