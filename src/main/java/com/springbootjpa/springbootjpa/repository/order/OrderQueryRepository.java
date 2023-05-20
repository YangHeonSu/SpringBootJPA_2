package com.springbootjpa.springbootjpa.repository.order;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager entityManager;

    /**
     * 주문 목록 조회 API (주문정보 + 주문상품)
     * JPA에서 DTO로 직접 조회할 때 컬렉션을 바로 넣을 수 없음
     * Order를 먼저 조회한 후 (toOne관계는 join을 통해서 조회)
     * 컬렉션인 OrderItem을 주문 Id(OrderId)를 통해 조회하여
     * 루프를 돌려서 OrderQueryDTO의 List<OrderQueryItemDTO> 설정
     *
     * @return List<OrderQueryDTO> findAll_OrderQueryDTO()
     */
    public List<OrderQueryDTO> findAll_OrderQueryDTO() {
        List<OrderQueryDTO> orderInfo = findOrders();
        // 1 + N 문제 발생
        // orderInfo는 query 1번으로 N개의 결과가 나오면
        // findOrderItemByOrderId(orderId)는 N번의 쿼리 실행
        orderInfo.forEach(orderQueryDTO -> orderQueryDTO.setOrderItems(findOrderItemByOrderId(orderQueryDTO.getOrderId())));
        return orderInfo;
    }

    /**
     * 주문 Id를 통해 주문 상품 목록 조회
     * findAllWithMemberDelivery()에서 컬렉션을 바로 넣을 수 없기 때문에 OrderItem을 조회하는 JPQL 생성
     *
     * @param orderId Long orderId
     * @return List<OrderQueryItemDTO> findOrderItem
     */
    public List<OrderQueryItemDTO> findOrderItemByOrderId(Long orderId) {
        return entityManager.createQuery("select new com.springbootjpa.springbootjpa.repository.order.OrderQueryItemDTO" +
                        "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId", OrderQueryItemDTO.class
                ).setParameter("orderId", orderId)
                .getResultList();
    }

    /**
     * 주문 조회 (주문 id, 주문자, 주문시간, 주문상태, 배달주소)
     * JPA에서 DTO 직접 조회
     * JPQL에서 컬렉션을 바로 넣을 수 없기 때문에 List<OrderQueryItemDTO>를 parameter로 받을 수 없음.
     *
     * @return List<OrderQueryDTO> 주문 목록 조회 (주문 id, 주문자, 주문시간, 주문상태, 배달주소)
     */
    public List<OrderQueryDTO> findOrders() {
        return entityManager.createQuery("select new com.springbootjpa.springbootjpa.repository.order.OrderQueryDTO" +
                "(o.id, m.name, o.orderDateTime, o.orderStatus, d.address) " +
                "from Order o " +
                "join o.member m " +
                "join o.delivery d", OrderQueryDTO.class
        ).getResultList();

    }
}