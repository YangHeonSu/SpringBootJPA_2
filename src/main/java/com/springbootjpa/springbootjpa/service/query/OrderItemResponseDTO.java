package com.springbootjpa.springbootjpa.service.query;

import com.springbootjpa.springbootjpa.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponseDTO {
    private String itemName;
    private int price;
    private int count;

    public OrderItemResponseDTO(OrderItem orderItem) {
        this.itemName = orderItem.getItem().getName();
        this.price = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
