package o.horbenko.fsm.utils;

import lombok.Data;
import o.horbenko.fsm.FsmStateHolder;

@Data
public class Order implements FsmStateHolder<OrderState> {

    private OrderState state;

    public Order(OrderState state) {
        this.state = state;
    }

    @Override
    public OrderState getState() {
        return state;
    }

    @Override
    public void setState(OrderState newState) {
        this.state = newState;
    }
}
