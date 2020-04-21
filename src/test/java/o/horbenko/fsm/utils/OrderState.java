package o.horbenko.fsm.utils;

public enum OrderState {

    NEW,

    PAYMENT_COLLECTED,
    PAYMENT_PROCESSING,
    PAYMENT_ERROR,

    INVOICE_CREATED,
    INVOICE_PROCESSING,
    INVOICE_PROCESSING_ERROR,
    INVOICE_REJECTED,

    PRODUCTS_SHIPPING,
    PRODUCTS_SHIPPING_PROCESSING,
    PRODUCTS_SHIPPING_ERROR,
    PRODUCTS_SHIPPED,

    ORDER_COMPLETE,
    ORDER_REJECTED;

}