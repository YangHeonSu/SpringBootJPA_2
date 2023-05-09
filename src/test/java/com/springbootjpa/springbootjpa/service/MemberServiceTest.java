package com.springbootjpa.springbootjpa.service;

import com.springbootjpa.springbootjpa.domain.Member;
import com.springbootjpa.springbootjpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void save() throws Exception {
        Member member = new Member();
        member.setName("yang");

        Long saveId = memberService.save(member);

        assertEquals(member, memberRepository.findOne(saveId));


    }


}