package o.horbenko.fsm.error;

public class NoPipelineFoundException extends RuntimeException {

    public NoPipelineFoundException(String pipelineId) {
        super("No FSM pipeline found by id = " + pipelineId);
    }

}
