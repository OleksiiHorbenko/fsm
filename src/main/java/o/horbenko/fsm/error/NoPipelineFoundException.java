package o.horbenko.fsm.error;

public class NoPipelineFoundException extends RuntimeException {
    private final String pipelineId;

    public NoPipelineFoundException(String pipelineId) {
        super("No FSM pipeline found by id = " + pipelineId);
        this.pipelineId = pipelineId;
    }
}
