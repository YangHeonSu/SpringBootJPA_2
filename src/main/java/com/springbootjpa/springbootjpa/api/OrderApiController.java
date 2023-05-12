package com.springbootjpa.springbootjpa.api;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import com.springbootjpa.springbootjpa.repository.OrderRepository;
import com.springbootjpa.springbootjpa.repository.OrderSearch;
import com.springbootjpa.springbootjpa.repository.ordersimple.OrderSimpleQueryDTO;
import com.springbootjpa.springbootjpa.repository.ordersimple.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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
     * 조회된 결과를 Entity로 받아서 DTO로 변환
     * 연관된 Entity를 모두 조회한 후 원하는 정보만 담은 DTO 생성 후 DTO로 변환하여 반환
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

    /**
     * 주문 목록 조회 API
     * 조회된 결과를 Entity로 받지 않고 직접 바로 DTO로 조회
     * API 스펙에 딱 맞게 가져옴
     * API 스펙 변경 시 JPQL 수정해야함
     * 장점으로는 성능 최적화
     * 단점으로는 재사용성 불가

     * @return List<OrderSimpleQueryDTO>
     */
    @GetMapping("/api/v3/orders")
    public List<OrderSimpleQueryDTO> findOrder3() {
        return orderSimpleQueryRepository.findAll();
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
