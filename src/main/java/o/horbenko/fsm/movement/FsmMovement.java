package o.horbenko.fsm.movement;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.movementaction.FsmMovementAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
