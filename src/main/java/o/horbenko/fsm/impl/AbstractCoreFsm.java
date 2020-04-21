package o.horbenko.fsm.impl;

import lombok.NonNull;
import o.horbenko.fsm.CoreFsm;
import o.horbenko.fsm.error.InvalidFsmConfigurationException;
import o.horbenko.fsm.movementaction.FsmMovementAction;
import o.horbenko.fsm.state.FsmState;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.error.NoMovementByTriggerInStateException;
import o.horbenko.fsm.movement.FsmMovement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @param <S> - state type
 * @param <T> - trigger (event) type
 * @param <D> - object that owns state
 */
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

        // 1. Get current state
        S initialState = stateHolder.getState();

        // 2. Get FsmState configuration
        FsmState<S, T, D> stateConfiguration = stateConfigurationMap.get(initialState);

        // 3. Get movement by trigger
        FsmMovement<S, T, D> movement = stateConfiguration
                .getMovementByTrigger(trigger)
                .orElseThrow(() -> new NoMovementByTriggerInStateException("Unable to find movement from state =" + initialState.toString() + " by trigger = " + trigger.toString()));

        // 4. Execute
        return moveWithExceptionHandling(movement, stateHolder);
    }


    private D moveWithExceptionHandling(FsmMovement<S, T, D> movement, D data) {
        S initialState = data.getState();
        try {

            data = applyActionIfExists(movement.getMovementAction(), data);
            data.setState(movement.getFutureStateOnSuccess());
            return applyActionIfExists(movement.getPostMovementAction(), data);

        } catch (Exception e) {
            System.out.println("Handled exception on movement from state = " + initialState);
            data.setState(initialState);
            return handleMovementException(movement, data, e);
        }
    }

    private D handleMovementException(FsmMovement<S, T, D> movement,
                                      D data,
                                      Exception e) {

        Optional<T> concreteExceptionTriggerOpt = movement.getTriggerByException(e.getClass());

        T nextMovementTrigger = concreteExceptionTriggerOpt
                .orElseGet(movement::getTriggerOnGeneralException);

        return move(nextMovementTrigger, data);
    }

    private D applyActionIfExists(Optional<FsmMovementAction<D>> actionOpt, D data) {
        if (actionOpt.isPresent()) {
            return actionOpt
                    .get()
                    .execute(data);
        } else {
            // do nothing
            return data;
        }
    }

}
