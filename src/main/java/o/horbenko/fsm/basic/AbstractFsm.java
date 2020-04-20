package o.horbenko.fsm.basic;

import o.horbenko.fsm.Fsm;
import o.horbenko.fsm.FsmState;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.error.NoMovementByTriggerInStateException;
import o.horbenko.fsm.movement.FsmMovement;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class AbstractFsm
        <S, T, D extends FsmStateHolder<S>>
        implements Fsm<S, T, D> {

    private Map<S, FsmState<S, T, D>> stateConfigurationMap;

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
        try {

            data = applyActionIfExists(movement.getMovementAction(), data);
            data.setState(movement.getFutureStateOnSuccess());
            data = applyActionIfExists(movement.getPostMovementAction(), data);
            return data;

        } catch (Exception e) {
            return handleMovementException(movement, data, e);
        }
    }

    private D handleMovementException(FsmMovement<S, T, D> movement,
                                      D data,
                                      Exception e) {

        Optional<T> concreteExceptionTriggerOpt = movement.getTriggerBy(e.getClass());

        T nextMovementTrigger = concreteExceptionTriggerOpt
                .orElseGet(movement::getTriggerByGeneralException);

        return move(nextMovementTrigger, data);
    }

    private D applyActionIfExists(Optional<UnaryOperator<D>> actionOpt, D data) {
        if (actionOpt.isPresent()) {
            return actionOpt
                    .get()
                    .apply(data);
        } else {
            // do nothing
            return data;
        }
    }

}
