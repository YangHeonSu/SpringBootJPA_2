package com.springbootjpa.springbootjpa.controller;

import com.springbootjpa.springbootjpa.domain.Address;
import com.springbootjpa.springbootjpa.domain.Member;
import com.springbootjpa.springbootjpa.domain.MemberDTO;
import com.springbootjpa.springbootjpa.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 등록 페이지
     *
     * @param model Model attribute
     * @return 회원 등록 페이지
     */
    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());

        return "member/memberForm";
    }

    /**
     * 회원 등록
     * 
     * @param memberDTO MemberDTO
     * @param bindingResult BindingResult
     * @return String redirect
     */
    @PostMapping("/members/new")
    public String save(@Validated MemberDTO memberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }
        Address address = new Address(memberDTO.getCity(), memberDTO.getStreet(), memberDTO.getZipcode());
        Member member = new Member();
        member.setName(memberDTO.getName()); // dto -> entity 변환 방법으로 변경 예정
        member.setAddress(address);

        memberService.save(member);

        return "redirect:/";
    }

    /**
     * 계정 조회
     *
     * @param model Model
     * @return memberList Page
     */
    @GetMapping("/members")
    public String findAll(Model model) {
        /*
         * Entity를 직접 노출 시키는 것은 추천안함
         * 원래는 Entity -> DTO 변환 후 model로 넘겨야함
         *
         */
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);

        return "member/memberList";
    }
    
    
}
