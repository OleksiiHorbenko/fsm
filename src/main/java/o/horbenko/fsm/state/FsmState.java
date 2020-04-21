package o.horbenko.fsm.state;

import lombok.NonNull;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.movement.FsmMovement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FsmState<S, T, D extends FsmStateHolder<S>> {

    private Map<T, FsmMovement<S, T, D>> possibleMovements;

    public FsmState() {
        this.possibleMovements = new HashMap<>();
    }

    public FsmState(@NonNull Map<T, FsmMovement<S, T, D>> possibleMovements) {
        this.possibleMovements = possibleMovements;
    }

    public FsmState<S, T, D> withMovement(@NonNull T onTrigger,
                                          FsmMovement<S, T, D> movement) {
        this.possibleMovements.put(onTrigger, movement);
        return this;
    }

    public Optional<FsmMovement<S, T, D>> getMovementByTrigger(T trigger) {
        return Optional.ofNullable(possibleMovements.get(trigger));
    }

}
