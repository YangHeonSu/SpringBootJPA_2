package com.springbootjpa.springbootjpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문가격
    private int count; // 주문 수량

    // 생성 메서드 //
    public static OrderItem createOrderItem(Item item, int orderPrice, int count ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        
        item.removeStock(count); // 주문 시 재고에서 주문량 만큼 제거

        return orderItem;
    }

    //== 비즈니스 로직 ==//
    /**
     * 주문 취소 시 재고 수량 원복
     */
    public void cancel() {
        getItem().addStock(count);
    }

    /**
     * 전체 주문 가격
     * @return
     */
    public int getTotalPrice() {
        return getOrderPrice()* getCount();
    }
}
