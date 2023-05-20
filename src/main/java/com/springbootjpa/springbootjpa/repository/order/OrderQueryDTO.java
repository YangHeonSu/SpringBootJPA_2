package com.springbootjpa.springbootjpa.repository.order;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class OrderQueryDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderQueryItemDTO> orderItems; // OrderItem이 Entity이기 때문에 OrderItem또한 DTO로 변환해야함.

    public OrderQueryDTO(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
