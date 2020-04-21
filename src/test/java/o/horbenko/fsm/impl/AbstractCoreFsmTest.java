package o.horbenko.fsm.impl;

import o.horbenko.fsm.FiniteStateMachine;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.configurer.FsmBuilder;
import o.horbenko.fsm.movementaction.FsmMovementAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AbstractCoreFsmTest {

    private FiniteStateMachine<String, String, Data> fsm;

    private FsmMovementAction<Data> action = spy(new MovementAction());
    private FsmMovementAction<Data> postMovementAction = spy(new MovementAction());

    @Before
    public void initFsm() {


        fsm = FsmBuilder.<String, String, Data>builder()

                // INITIAL
                .andState("INITIAL")

                // INITIAL --E1--> E_E1
                // INITIAL --E_E1-> (on exception)
                .andStateMovementBy("E1")
                .targetStateOnSuccess("S1")
                .movementAction(action)
                .postMovementAction(postMovementAction)
                .exceptionTrigger("E_E1")

                // INITIAL --E_E1--> S_ERR_1
                .andStateMovementBy("E_E1")
                .targetStateOnSuccess("S_ERR_1")
                .postMovementAction(postMovementAction)

                // S_ERR_1
                .andState("S_ERR_1")

                // S1
                .andState("S1")

                .build();
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
