package o.horbenko.fsm.movementaction;

public interface FsmMovementAction<D> {
    D execute(D data);
}
