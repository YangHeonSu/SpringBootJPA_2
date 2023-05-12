package com.springbootjpa.springbootjpa.repository.ordersimple;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    /**
     * OrderRepository 에서는 Order Entity를 반환해야함
     * OrderSimpleQueryDTO처럼 DTO로 직접 반환하기 위해서 API 스펙에 딱 맞게 뿌려 줄 수 있는 Repository 생성하여 관리
     * 유지보수 향상
     */

    private final EntityManager entityManager;

    /**
     * JPQL를 통해서 원하는 값만 조회하여 DTO로 직접 반환
     * API에 딱 맞게 가져옴
     * 단점으로는 재사용성 불가
     * 
     * @return
     */
    public List<OrderSimpleQueryDTO> findAll() {
       return entityManager.createQuery(
                "select new com.springbootjpa.springbootjpa.repository.ordersimple.OrderSimpleQueryDTO(o.id, m.name, o.orderDateTime, o.orderStatus, d.address)" +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d", OrderSimpleQueryDTO.class)
                .getResultList();
    }
}
