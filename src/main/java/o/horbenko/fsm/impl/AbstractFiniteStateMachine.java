package o.horbenko.fsm.impl;

import lombok.NonNull;
import o.horbenko.fsm.FiniteStateMachine;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.pipeline.FsmPipeline;
import o.horbenko.fsm.state.FsmState;

import java.util.Map;
import java.util.Optional;

public class AbstractFiniteStateMachine
        <S, T, D extends FsmStateHolder<S>>
        extends AbstractCoreFsm<S, T, D>
        implements FiniteStateMachine<S, T, D> {

    public AbstractFiniteStateMachine() {
        super();
    }

    public AbstractFiniteStateMachine(Map<S, FsmState<S, T, D>> stateConfigurationMap) {
        super(stateConfigurationMap);
    }

    /**
     * StateMachine tries to catch all exceptions in transition action, but will throw exception
     * if no exception logic found or on transition to exception state.
     * Continues execution in case, when current state is defined in {@link FsmPipeline}
     * <p>
     * <b>WARNING!</b>
     * Can cause infinite loop processing in case of wrong {@link FiniteStateMachine} or {@link FsmPipeline}
     * configurations (configured FSM not matches finite state machine rules)
     *
     * @param initialDataToProcess context of current state (initial state)
     * @param pipelineToMoveOn     pipeline configuration
     * @return context that contains execution result and updated state.
     * @throws RuntimeException if <ul>
     *                          <li>1. FSM not configured properly (exists loop without exit-point state)</li>
     *                          <li>2. {@link FsmPipeline} not configured properly
     *                          (exists loop without exit-point state)</li>
     *                          </ul>
     */
    @Override
    public D moveOnPipeline(@NonNull D initialDataToProcess,
                            @NonNull FsmPipeline<S, T> pipelineToMoveOn) {

        D result = initialDataToProcess;

        /*
         * Terminates in case, when pipeline does not contains trigger for next movement by current state
         * */
        while (true) {

            Optional<T> movementTriggerOpt = pipelineToMoveOn
                    .getTriggerForNextMovementByState(result.getState());

            if (movementTriggerOpt.isEmpty()) // exit point
                return result;
            else
                result = move(movementTriggerOpt.get(), result);
        }
    }

}
