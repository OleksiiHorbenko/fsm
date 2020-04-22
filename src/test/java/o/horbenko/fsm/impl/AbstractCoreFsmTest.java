package o.horbenko.fsm.impl;

import o.horbenko.fsm.FiniteStateMachine;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.configurer.FsmBuilder;
import o.horbenko.fsm.error.NoMovementByTriggerInStateException;
import o.horbenko.fsm.movementaction.FsmMovementAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AbstractCoreFsmTest {

    private FiniteStateMachine<String, String, Data> fsm;

    private FsmMovementAction<Data> action = spy(new MovementAction());
    private FsmMovementAction<Data> errorStatePostMovementAction = spy(new MovementAction());
    private FsmMovementAction<Data> postMovementAction = spy(new MovementAction());

    @Before
    public void initFsm() {

        fsm = FsmBuilder.<String, String, Data>builder()

                // INITIAL
                .andState("INITIAL")

                // INITIAL --E1-> S1
                // INITIAL --E_E1-> (on exception) S_ERR_1
                .andStateMovementBy("E1")
                .targetStateOnSuccess("S1")
                .movementAction(action)
                .postMovementAction(postMovementAction)
                .exceptionTrigger(CustomException.class, "E_CUSTOM")
                .exceptionTrigger("E_E1")

                // INITIAL --E_E1--> S_ERR_1
                .andStateMovementBy("E_E1")
                .targetStateOnSuccess("S_ERR_1")
                .postMovementAction(errorStatePostMovementAction)

                // INITIAL -> E_CUSTOM -> S_CUSTOM_ERROR
                .andStateMovementBy("E_CUSTOM")
                .targetStateOnSuccess("S_CUSTOM_ERROR")

                // S_ERR_1
                .andState("S_ERR_1")
                .andState("S_CUSTOM_ERROR")
                .andState("S1")

                .buildFsm();
    }

    @Test
    public void testMove_ok() {

        // ARRANGE
        Data initial = new Data("INITIAL");
        String expectedState = "S1";

        // ACT
        Data actual = fsm.move("E1", initial);

        // ASSERT
        Assert.assertEquals(expectedState, actual.getState());
    }

    @Test
    public void testMove_exceptionInAction() {

        // ARRANGE
        Data initial = new Data("INITIAL");
        String expectedState = "S_ERR_1";

        when(action.execute(initial)).thenThrow(new RuntimeException());

        // ACT
        Data actual = fsm.move("E1", initial);

        // ASSERT
        Assert.assertEquals(expectedState, actual.getState());
    }


    @Test
    public void testMove_exceptionInPostAction() {

        // ARRANGE
        Data initial = new Data("INITIAL");
        String expectedState = "S_ERR_1";

        when(postMovementAction.execute(initial)).thenThrow(new RuntimeException());

        // ACT
        Data actual = fsm.move("E1", initial);

        // ASSERT
        Assert.assertEquals(expectedState, actual.getState());
    }

    @Test(expected = NoMovementByTriggerInStateException.class)
    public void testMove_ExceptionInActionAndInErrorAction() {
        // ARRANGE
        Data initial = new Data("INITIAL");
        String expectedState = "INITIAL";

        when(action.execute(initial)).thenThrow(new RuntimeException());
        when(errorStatePostMovementAction.execute(initial)).thenThrow(new RuntimeException());

        // ACT
        Data actual = fsm.move("E1", initial);

        // ASSERT
        Assert.assertEquals(expectedState, actual.getState());
    }

    @Test
    public void testMove_CustomExceptionInAction() {
        // ARRANGE
        Data initial = new Data("INITIAL");
        String expectedState = "S_CUSTOM_ERROR";

        when(action.execute(initial)).thenThrow(new CustomException());

        // ACT
        Data actual = fsm.move("E1", initial);

        // ASSERT
        Assert.assertEquals(expectedState, actual.getState());
    }


    static class CustomException extends RuntimeException {
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
            return data;
        }
    }


}
