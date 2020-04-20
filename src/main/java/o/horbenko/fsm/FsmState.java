package o.horbenko.fsm;

import o.horbenko.fsm.movement.FsmMovement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FsmState<S, T, D extends FsmStateHolder<S>> {

    private Map<T, FsmMovement<S, T, D>> possibleMovements;

    public FsmState() {
        this.possibleMovements = new HashMap<>();
    }

    public Optional<FsmMovement<S, T, D>> getMovementByTrigger(T trigger) {
        return Optional.ofNullable(possibleMovements.get(trigger));
    }

}
