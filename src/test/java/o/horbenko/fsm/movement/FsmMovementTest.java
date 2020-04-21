package o.horbenko.fsm.movement;

import o.horbenko.fsm.FsmStateHolder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FsmMovementTest {

    private FsmMovement<String, String, Data> toTest;

    @Before
    public void setUpMovement() {
        toTest = new FsmMovement<String, String, Data>()
                .triggerOnException(CustomException.class, "TRIGGER_ON_CUSTOM_EXCEPTION")
                .triggerOnGeneralException("TRIGGER_ON_EXCEPTION");
    }

    @Test
    public void testGetTriggerByRuntimeException() {

        // ACT
        String actual = toTest
                .getTriggerOnGeneralException();

        // ASSERT
        assertEquals("TRIGGER_ON_EXCEPTION", actual);
    }

    @Test
    public void testGetTriggerByCustomException() {

        // ACT
        String actual = toTest
                .getTriggerByConcreteException(CustomException.class)
                .get();

        // ASSERT
        assertEquals("TRIGGER_ON_CUSTOM_EXCEPTION", actual);
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
}
