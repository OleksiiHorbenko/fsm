package o.horbenko.fsm.utils;

import lombok.Data;
import o.horbenko.fsm.FiniteStateMachine;
import o.horbenko.fsm.configurer.FsmBuilder;
import o.horbenko.fsm.pipeline.FsmPipeline;

@Data
public class OrderFsm {

    private FiniteStateMachine<OrderState, OrderEvents, Order> fsm;
    private FsmPipeline<OrderState, OrderEvents> mainline;

    public void initDefault() {
        this.fsm = FsmBuilder
                .<OrderState, OrderEvents, Order>builder()

                // NEW
                .andState(OrderState.NEW)
                // NEW -> PROCESS_PAYMENT
                .andStateMovementBy(OrderEvents.PROCESS_PAYMENT)
                .targetStateOnSuccess(OrderState.PAYMENT_PROCESSING)
                // NEW -> PROCESS_INVOICE
                .andStateMovementBy(OrderEvents.PROCESS_INVOICE)
                .targetStateOnSuccess(OrderState.INVOICE_PROCESSING)
                .exceptionTrigger(OrderEvents.ERROR)
                // NEW -> PROCESS_SHIPMENT
                .andStateMovementBy(OrderEvents.PROCESS_SHIPMENT)
                .targetStateOnSuccess(OrderState.PRODUCTS_SHIPPING)
                .exceptionTrigger(OrderEvents.ERROR)

                .build();

        mainline = new FsmPipeline<OrderState, OrderEvents>()
                .addPipe(OrderState.NEW, OrderEvents.PROCESS_INVOICE);
    }

    public Order processMainline(Order orderToProcess) {
        return fsm.moveOnPipeline(orderToProcess, mainline);
    }

}
