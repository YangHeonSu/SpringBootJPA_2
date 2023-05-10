package com.springbootjpa.springbootjpa.api;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import com.springbootjpa.springbootjpa.repository.OrderRepository;
import com.springbootjpa.springbootjpa.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 주문 목록 조회 API
     * N + 1문제 존재 (Lazy 초기화 현상)
     * @return List<SimpleOrderDTO>
     */
    @GetMapping("/api/v1/orders")
    public List<SimpleOrderDTO> findOrder() {
        List<Order> orders = orderRepository.findAll(new OrderSearch()); // DB에서 주문 조회
        return orders
                .stream()
                .map(SimpleOrderDTO::new)
                .collect(toList());
    }

    /**
     * 주문 목록 조회 API
     * N + 1문제 해결 Fetch Join 사용
     * @return List<SimpleOrderDTO>
     */
    @GetMapping("/api/v2/orders")
    public List<SimpleOrderDTO> findOrder2() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders
                .stream()
                .map(SimpleOrderDTO::new)
                .collect(toList());
    }

    @Data
    static class SimpleOrderDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // Order Entity -> SimpleOrderDTO 변환
        // Entity 노출을 막기 위함
        public SimpleOrderDTO(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDateTime();
            this.orderStatus = order.getOrderStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}
