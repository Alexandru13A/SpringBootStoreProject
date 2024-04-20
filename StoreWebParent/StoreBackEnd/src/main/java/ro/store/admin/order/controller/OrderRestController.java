package ro.store.admin.order.controller;

import lombok.Getter;
import lombok.Setter;
import ro.store.admin.order.OrderService;

public class OrderRestController {

    private OrderService service;

    public OrderRestController(OrderService service) {
        this.service = service;
    }

    // @PostMapping("/orders_shipper/update/{id}/{status}")
    // public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable("status") String status) {
    //     service.updateOrderStatus(orderId, status);
    //     return new Response(orderId, status);
    // }

}

@Getter
@Setter
class Response {
    private Integer orderId;
    private String status;

    public Response(Integer orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

}
