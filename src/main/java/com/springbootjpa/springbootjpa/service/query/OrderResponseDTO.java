package com.springbootjpa.springbootjpa.service.query;

import com.springbootjpa.springbootjpa.api.OrderApiController;
import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemResponseDTO> orderItems; // OrderItem이 Entity이기 때문에 OrderItem또한 DTO로 변환해야함.

    public OrderResponseDTO(Order order) {
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDateTime();
        this.orderStatus = order.getOrderStatus();
        this.address = order.getDelivery().getAddress();
        this.orderItems = order.getOrderItems()  // 지연로딩 발생한 것이 proxy 초기화
                .stream()
                .map(OrderItemResponseDTO::new)
                .collect(Collectors.toList());
    }
}
