package o.horbenko.fsm.movement;

import lombok.Data;
import lombok.experimental.Accessors;
import o.horbenko.fsm.FsmStateHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Data
@Accessors(chain = true)
public class FsmMovement<S, T, D extends FsmStateHolder<S>> {

    private UnaryOperator<D> movementAction;
    private UnaryOperator<D> postMovementAction;

    private S futureStateOnSuccess;

    // exceptions handling
    private Map<Class<? extends Exception>, T> triggerByException;
    private T triggerOnGeneralException;


    public FsmMovement() {
        this.triggerByException = new HashMap<>();
    }

    public FsmMovement<S, T, D> addTriggerOnException(Class<? extends Exception> exceptionType,
                                                      T triggerOnException) {
        this.triggerByException.put(exceptionType, triggerOnException);
        return this;
    }

    public Optional<T> getTriggerBy(Class<? extends Exception> exceptionType) {
        return Optional.ofNullable(triggerByException.get(exceptionType));
    }

    public S getStateOnSuccessfulMovement() {
        return futureStateOnSuccess;
    }


    public Optional<UnaryOperator<D>> getMovementAction() {
        return Optional.ofNullable(movementAction);
    }

    public Optional<UnaryOperator<D>> getPostMovementAction() {
        return Optional.ofNullable(postMovementAction);
    }


    public T getTriggerByGeneralException() {
        return triggerOnGeneralException;
    }
}
