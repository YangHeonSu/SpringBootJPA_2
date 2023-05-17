package com.springbootjpa.springbootjpa.repository;

import com.springbootjpa.springbootjpa.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager entityManager;

    public void save(Order order) {
        entityManager.persist(order);
    }

    public Order findOne(Long id) {
        return entityManager.find(Order.class, id);
    }

    /**
     * JPA 동적 쿼리를 위한 Criteria
     * 실무에서는 사용 못함 - 유지보수 매우 힘듬
     * 실무에서는 QueryDsl 사용
     *
     * @param orderSearch
     * @return
     */
    public List<Order> findAll(OrderSearch orderSearch) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> orderCriteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> orderRoot = orderCriteriaQuery.from(Order.class);
        Join<Object, Object> join = orderRoot.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = criteriaBuilder.equal(orderRoot.get("orderStatus"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = criteriaBuilder.like(join.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }


        orderCriteriaQuery.where(criteriaBuilder.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = entityManager.createQuery(orderCriteriaQuery).setMaxResults(1000);
        return query.getResultList();
    }

    /**
     * 주문 목록 조회
     * 계정과 배달 테이블 조인
     *
     * @return List<Order> findAllWithMemberDelivery
     */
    public List<Order> findAllWithMemberDelivery() {
        return entityManager.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class)
                .getResultList();
    }

    /**
     * 주문 목록 조회
     * 계정과 배달 테이블 조인
     * 주문 상품 테이블 조인 ( 1 : N)
     * 1 + N 문제를 해결하기 위한 Collection Fetch Join
     * 1 : N 을 조인하기 때문에 주문이 1개 당 2개 상품 주문 시 총 4개의 결과가 조회
     * 중복을 제거하기 위해 Distinct 키워드 사용
     * SQL Distinct 기능(모든 데이트가 같아야 중복제거) + 같은 Entity가 조회되면 애플리케이션에서 Entity 중복 제거
     *
     * 단점으로는 페이징 불가
     * Collection Fetch Join은 한 번만 사용해야함
     *
     * @return List<Order> findAllWithOrderItem()
     */
    public List<Order> findAllWithOrderItem() {
        return entityManager.createQuery(
                "select distinct o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i", Order.class)
                .getResultList();
    }

    /**
     * 주문 목록 페이징 조회 API
     * Entity -> DTO 변환
     * Entity -> DTO 변환 시 Order 뿐만 아니라 연관 관계인 OrderItem 또한 DTO 변환해줘야함.
     * Collection Fetch Join 의 페이징 불가 문제를 해결
     * toOne 관계는 Fetch Join ( toOne 관계는 row 수를 증가하지 않기 때문
     *                                    || toMany 관계는 row 수 증가 주문이 1개 당 2개 상품 주문 시 총 4개의 결과가 조회)
     * 컬렉션은 지연 로딩으로 조회 (Lazy) -> 1 + N 문제 발생   [컬렉션 -> orderItem item]??
     * 1 + N 문제를 해결하기 위해 지연로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size 사용
\\
     * @param offset int offset (초기값 0) 시작점
     * @param limit int limit 몇 개씩 가져올지
     * @return List<Order> orders
     */
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return entityManager.createQuery(
                        "select o from Order o " +
                                "join fetch o.member m " +
                                "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
