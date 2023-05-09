package com.springbootjpa.springbootjpa.service;

import com.springbootjpa.springbootjpa.domain.Member;
import com.springbootjpa.springbootjpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     *
     * @param member member
     * @return Long member_id (pk)
     */
    @Transactional
    public Long save(Member member) {

        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 모든 회원 조회
     *
     * @return List<Member> findAll
     */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * 회원 상세 조회
     *
     * @param id Long id (pk)
     * @return Member
     */
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    /*
     * 중복 회원 검증
     * Spring Data Jpa를 사용하여 Optional 사용하는 것이 좋지만
     * 강의에서 하는 것은 JPA
     */
    private void validateDuplicateMember(Member member) {
        List<Member> members = memberRepository.findByName(member.getName());
        if (!members.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public void update(Long id, String name) {
        
        // JPA 수정 시에는 변경 감지 이용
        
        Member member = memberRepository.findOne(id);
        member.setName(name); 
    }

}
