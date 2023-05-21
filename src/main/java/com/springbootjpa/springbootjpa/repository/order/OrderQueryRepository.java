package com.springbootjpa.springbootjpa.repository.order;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<OrderQueryDTO> findAllV3_OrderQueryDTO() {
        List<OrderQueryDTO> orderInfo = findOrders();
        // 1 + N 문제 발생
        // orderInfo는 query 1번으로 N개의 결과가 나오면
        // findOrderItemByOrderId(orderId)는 N번의 쿼리 실행
        orderInfo.forEach(orderQueryDTO -> orderQueryDTO.setOrderItems(findOrderItemByOrderId(orderQueryDTO.getOrderId())));
        return orderInfo;
    }

    /**
     * 주문 목록 조회 API (주문정보 + 주문상품)
     * JPA에서 DTO로 직접 조회할 때 컬렉션을 바로 넣을 수 없음
     * Order를 먼저 조회한 후 (toOne관계는 join을 통해서 조회)
     * 컬렉션인 OrderItem을 주문 Id(OrderId)를 Join을 통해 where in으로 조회
     * 조회된 OrderItem을 Map<Long, List<OrderQueryItemDTO>>형태로 변환 후
     * 루프를 돌려서 OrderQueryDTO의 List<OrderQueryItemDTO> 설정
     * 1 + N 문제 해결 -> 1 + 1 쿼리 발생 (Query 1번 컬렉션 1번) 
     * ex) 주문과 toOne 관계인 Member, Delivery join 쿼리 1
     * ex) 주문과 toMany 관계인 OrderItem 조회 쿼리 1번
     * 
     * @return List<OrderQueryDTO> findAll_OrderQueryDTO()
     */
    public List<OrderQueryDTO> findAllV4_OrderQueryDTO() {

        List<OrderQueryDTO> orderInfo = findOrders();  // 주문 정보 (주문정보 + 주문자 + 배송정보) 
        List<Long> orderIds = getOrderIds(orderInfo); // 주문 아이디를 List 형태로 가져오기 (주문 상품정보를 in절을 통해 가져오기 위함)
        List<OrderQueryItemDTO> orderItems = getOrderItems(orderIds); // 주문 Id List를 in절을 사용하여 주문 상품 조회
        
        // setOrderItemsMap => in 절을 통해 가져온 주문 상품 정보를  Map 형태로 변환
        orderInfo.forEach(orderQueryDTO -> orderQueryDTO.setOrderItems(setOrderItemsMap(orderItems).get(orderQueryDTO.getOrderId())));
        return orderInfo;
    }

    /**
     * 주문 Id(Order TB의 Id) List 조회
     *
     * @param orderQueryDTOS List<OrderQueryDTO> orderQueryDTOS
     * @return List<Long> orderIds
     */
    public List<Long> getOrderIds(List<OrderQueryDTO> orderQueryDTOS) {
        return orderQueryDTOS.stream()
                .map(OrderQueryDTO::getOrderId)
                .collect(Collectors.toList());
    }

    /**
     * 주문 Id(Order TB의 Id) List를 통해 주문 상품 조회
     * in절을 사용하여 한 번의 쿼리로 모든 주문 상품 조회
     *
     * @param orderIds List<Long> orderIds
     * @return List<OrderQueryItemDTO> orderItems
     */
    public List<OrderQueryItemDTO> getOrderItems(List<Long> orderIds) {
        return entityManager.createQuery("select new com.springbootjpa.springbootjpa.repository.order.OrderQueryItemDTO" +
                        "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id in :orderIds", OrderQueryItemDTO.class
                ).setParameter("orderIds", orderIds)
                .getResultList();

    }

    /**
     * in절을 사용하여 orderId에 대한 주문 상품을 조회하여 Map으로 변환
     *
     * @param orderItems List<OrderQueryItemDTO> orderItems
     * @return Map<Long, List < OrderQueryItemDTO>> orderItemsMap
     */
    public Map<Long, List<OrderQueryItemDTO>> setOrderItemsMap(List<OrderQueryItemDTO> orderItems) {
        return orderItems.stream().collect(Collectors.groupingBy(OrderQueryItemDTO::getOrderId));
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