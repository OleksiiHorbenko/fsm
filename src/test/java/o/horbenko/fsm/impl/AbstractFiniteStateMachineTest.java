package o.horbenko.fsm.impl;

import o.horbenko.fsm.FiniteStateMachine;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.configurer.FsmBuilder;
import o.horbenko.fsm.error.NoMovementByTriggerInStateException;
import o.horbenko.fsm.movementaction.FsmMovementAction;
import o.horbenko.fsm.pipeline.FsmPipeline;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AbstractFiniteStateMachineTest {

    private FiniteStateMachine<String, String, Data> fsm;

    private MovementAction actionOnS2S3 = spy(new MovementAction());
    private MovementAction errorStatePostMovementAction = spy(new MovementAction());


    @Before
    public void initFsm() {
        fsm = FsmBuilder.<String, String, Data>builder()

                // S1
                .andState("S1")

                .andStateMovementBy("TS1S2")
                .targetStateOnSuccess("S2")

                // S2
                .andState("S2")
                .andStateMovementBy("TS2S3")
                .movementAction(actionOnS2S3)
                .exceptionTrigger("ERR")
                .targetStateOnSuccess("S3")

                .andStateMovementBy("TS2S4")
                .targetStateOnSuccess("S4")

                .andStateMovementBy("ERR")
                .postMovementAction(errorStatePostMovementAction)
                .targetStateOnSuccess("S_ERROR_S2S3")


                // S3
                .andState("S3")

                .andStateMovementBy("TS3S1")
                .targetStateOnSuccess("S1")

                .andStateMovementBy("TS3S4")
                .targetStateOnSuccess("S4")

                // S4
                .andState("S4")
                .andStateMovementBy("TS4S1")
                .targetStateOnSuccess("S1")

                // S_ERROR_S2S3
                .andState("S_ERROR_S2S3")
                .andStateMovementBy("TS_ERROR_S2S3S1")
                .targetStateOnSuccess("S1")

                .andState("S_ERROR_S1S2")
                .andState("S_ERROR_S3S4")
                .buildFsm();
    }

    @Test
    public void testMoveOnPipeline_s1s2s3s4() {

        // ARRANGE
        FsmPipeline<String, String> s1s2s3s4 = new FsmPipeline<String, String>()
                .addPipe("S1", "TS1S2")
                .addPipe("S2", "TS2S3")
                .addPipe("S3", "TS3S4");

        Data initial = new Data("S1");
        String expectedState = "S4";

        // ACT
        Data result = fsm.moveOnPipeline(initial, s1s2s3s4);

        // ASSERT
        assertEquals(expectedState, result.getState());
    }

    @Test
    public void testMoveOnPipeline_preventRecursion() {

        // ARRANGE
        FsmPipeline<String, String> s1s2s3s4 = new FsmPipeline<String, String>()
                .addPipe("S1", "TS1S2")
                .addPipe("S2", "TS2S3")
                .addPipe("S3", "TS3S4");

        Data initial = new Data("S1");
        when(actionOnS2S3.execute(initial)).thenThrow(new RuntimeException());
        String expectedState = "S_ERROR_S2S3";

        // ACT
        Data result = fsm.moveOnPipeline(initial, s1s2s3s4);

        // ASSERT
        assertEquals(expectedState, result.getState());
    }

    @Test(expected = NoMovementByTriggerInStateException.class)
    public void testMoveOnPipeline_exception() {

        // ARRANGE
        FsmPipeline<String, String> s1s2s3s4 = new FsmPipeline<String, String>()
                .addPipe("S1", "TS1S2")
                .addPipe("S2", "TS2S3")
                .addPipe("S3", "TS3S4");

        Data initial = new Data("S1");
        when(actionOnS2S3.execute(initial)).thenThrow(new RuntimeException());
        when(errorStatePostMovementAction.execute(initial)).thenThrow(new RuntimeException());

        // ACT
        fsm.moveOnPipeline(initial, s1s2s3s4);
    }


    public static class Data implements FsmStateHolder<String> {
        private String state;

        public Data(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static class MovementAction implements FsmMovementAction<Data> {

        @Override
        public Data execute(Data data) {
            System.out.println("action -> " + data.getState());
            return data;
        }
    }

}
