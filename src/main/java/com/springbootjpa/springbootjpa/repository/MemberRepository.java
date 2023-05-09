package com.springbootjpa.springbootjpa.repository;

import com.springbootjpa.springbootjpa.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // @PersistenceContext // jpa 표준 어노테이션
    private final EntityManager entityManager;

    /**
     * 회원 저장
     *
     * @param member Member id, username
     */
    public void save(Member member) {
        entityManager.persist(member); // 회원 저장 JPA
    }

    /**
     * Id(pk 값)으로 회원 정보 찾기
     *
     * @param id Long Id (= pk)
     * @return Member member
     */
    public Member findOne(Long id) {
        return entityManager.find(Member.class, id);
    }

    /**
     * 모든 회원 조회 (JPA 에서는 createQuery JPQL을 사용)
     *
     * @return List<Member> findAll
     */
    public List<Member> findAll() {
        return entityManager.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name) {
        return entityManager.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
