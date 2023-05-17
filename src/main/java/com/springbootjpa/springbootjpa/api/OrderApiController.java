package com.springbootjpa.springbootjpa.api;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Order;
import com.springbootjpa.springbootjpa.domain.OrderItem;
import com.springbootjpa.springbootjpa.domain.OrderStatus;
import com.springbootjpa.springbootjpa.repository.OrderRepository;
import com.springbootjpa.springbootjpa.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 주문 목록 Collection 조회 API
     * 주문자, 주문상태, 주문일, 배송정보, 주문 아이템 정보
     * Entity -> DTO 변환
     * Entity -> DTO 변환 시 Order 뿐만 아니라 연관 관계인 OrderItem 또한 DTO 변환해줘야함.
     * 1 + N 문제 해결을 위해 Collection Fetch Join
     * 1 : N 을 조인하기 때문에 주문이 1개 당 2개 상품 주문 시 총 4개의 결과가 조회
     * 중복을 제거하기 위해 Distinct 키워드 사용
     * SQL Distinct 기능(모든 데이트가 같아야 중복제거) + 같은 Entity가 조회되면 애플리케이션에서 Entity 중복 제거
     * @return List<OrderResponseDTO> orders
     */
    @GetMapping("/api/v2/orders")
    public List<OrderResponseDTO> findAllV2() {
        List<Order> all = orderRepository.findAllWithOrderItem();
        // Entity -> DTO 변환 후 반환
        return all.stream().map(OrderResponseDTO::new).collect(Collectors.toList());
    }

    /**
     * 주문 목록 페이징 조회 API
     * 컬렉션 엔티티 + 페이징 조회
     * Entity -> DTO 변환
     * Entity -> DTO 변환 시 Order 뿐만 아니라 연관 관계인 OrderItem 또한 DTO 변환해줘야함.
     * Collection Fetch Join 의 페이징 불가 문제를 해결
     * toOne 관계는 Fetch Join ( toOne 관계는 row 수를 증가하지 않기 때문
     *                                    || toMany 관계는 row 수 증가 주문이 1개 당 2개 상품 주문 시 총 4개의 결과가 조회)
     * 컬렉션은 지연 로딩으로 조회 (Lazy) -> 1 + N 문제 발생
     * 1 + N 문제를 해결하기 위해 지연로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size 사용
     * @return List<OrderResponseDTO> orders
     */
    @GetMapping("/api/v2.1/orders")
    public List<OrderResponseDTO> findAllV2_Page(
            @RequestParam(name = "offset", defaultValue = "0") int offset
            ,@RequestParam(name = "limit", defaultValue = "100") int limit) {
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);
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
            this.orderItems = order.getOrderItems()  // 지연로딩 발생한 것이 proxy 초기화
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
