package ro.store.admin.order.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import ro.store.admin.order.OrderService;

@RestController
public class OrderRestController {

    private OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders_shipper/update/{id}/{status}")
    public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable("status") String status) {
        orderService.updateStatus(orderId, status);
        return new Response(orderId, status);
    }

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
