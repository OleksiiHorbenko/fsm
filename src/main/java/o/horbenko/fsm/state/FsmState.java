package o.horbenko.fsm.state;

import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.movement.FsmMovement;

import java.util.HashMap;
import java.util.Map;

public class FsmState<S, T, D extends FsmStateHolder<S>> {

    private Map<T, FsmMovement<S, T, D>> availableMovementsMap;

    public FsmState() {
        this.availableMovementsMap = new HashMap<>();
    }


}
