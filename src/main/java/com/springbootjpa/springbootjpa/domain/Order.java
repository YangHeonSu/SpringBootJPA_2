package com.springbootjpa.springbootjpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // fk 설정
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // cascade = CascadeType.ALL == Order가  persist되면 OrderItem도 persist
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // cascade = CascadeType.ALL == Order가  persist되면 Delivery도 persist
    @JoinColumn(name = "delivery_id") // fk 설정
    private Delivery delivery;

    private LocalDateTime orderDateTime; // 날짜 + 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태 [ ORDER, CANCEL]

    /**
     * 양방향일 경우
     * 연관관계 편의 Method
     */
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // == 생성 메서드 == //
    public static Order createOrder(Member member , Delivery delivery , OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDateTime(LocalDateTime.now());

        return  order;
    }


    //== 비즈니스 로직 ==//
    /*
     * 주문 취소
     */
    public void cancel() {
        // 이미 배송 완료 일 경우
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setOrderStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // 조회 로직 //
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
/*        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;*/
    }

}
