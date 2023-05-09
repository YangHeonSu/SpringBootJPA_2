package com.springbootjpa.springbootjpa.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    /**
     * 테스트 클래스 Transactional 어노테이션이 있으면 DB에 값이 rollback되어 저장은 안됨.
     * rollback(false)를 통해 막을 수는 있음.
     */

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    public void saveMember() throws Exception {
        //given
/*        Member member = new Member();
        member.setUsername("memberA");
        //when
        Long memberId = memberRepository.save(member);
        Member byId = memberRepository.findById(memberId);

        //then
        Assertions.assertThat(byId.getId()).isEqualTo(member.getId());
        Assertions.assertThat(byId.getUsername()).isEqualTo(member.getUsername());*/
    }

}