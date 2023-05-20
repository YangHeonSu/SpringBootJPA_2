package com.springbootjpa.springbootjpa.repository.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class OrderQueryItemDTO {

    @JsonIgnore // orderId는 결과로 반영 x
    private Long orderId;
    private String orderName;
    private int orderPrice;
    private int orderCount;

    public OrderQueryItemDTO(Long orderId, String orderName, int orderPrice, int orderCount) {
        this.orderId = orderId;
        this.orderName = orderName;
        this.orderPrice = orderPrice;
        this.orderCount = orderCount;
    }
}
