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
}
