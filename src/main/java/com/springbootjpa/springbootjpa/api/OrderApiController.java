package com.springbootjpa.springbootjpa.api;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.domain.OrderItem;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import com.springbootjpa.springbootjpa.repository.OrderRepository;
import com.springbootjpa.springbootjpa.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    /**
     * 주문 목록 Collection 조회 API
     * 주문자, 주문상태, 주문일, 배송정보, 주문 아이템 정보
     * Entity -> DTO 변환
     * Entity -> DTO 변환 시 Order 뿐만 아니라 연관 관계인 OrderItem 또한 DTO 변환해줘야함.
     * 1 + N 문제 존재
     * @return List<OrderResponseDTO> orders
     */
    @GetMapping("/api/v1/orders")
    public List<OrderResponseDTO> findAllV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        
        // Entity -> DTO 변환 후 반환
        return all.stream().map(OrderResponseDTO::new).collect(Collectors.toList());
    }

    @Getter
    static class OrderResponseDTO {
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
            this.orderItems = order.getOrderItems()
                    .stream()
                    .map(OrderItemResponseDTO::new)
                    .collect(Collectors.toList());
            
        }
    }

    @Getter
    static class OrderItemResponseDTO {

        private String itemName;
        private int price;
        private int count;

        public OrderItemResponseDTO(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.price = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }


}
