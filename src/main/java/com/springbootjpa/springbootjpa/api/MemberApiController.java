package com.springbootjpa.springbootjpa.api;

import com.springbootjpa.springbootjpa.domain.Member;
import com.springbootjpa.springbootjpa.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 계정 조회 API
     *
     * @return Result int count, List<findMemberResponse> members
     */
    @GetMapping("/api/v2/members")
    public Result findAll() {

        // Result라는 클래스로 감싸서 return Map<String, Object> 형태와 비슷

        List<Member> findMembers = memberService.findAll();
        List<findMemberResponse> members = findMembers
                .stream()
                .map(m -> new findMemberResponse(
                        m.getName()
                        , m.getAddress().getCity()
                        , m.getAddress().getStreet()
                        , m.getAddress().getZipcode()))
                .collect(Collectors.toList());

        return new Result(members.size(), members);
    }


    /**
     * 회원 등록 API
     *
     * @param createMemberRequest createMemberRequest name (MemberDTO를 사용하지 않고 간단한 예제를 위함)
     * @return CreateMemberResponse Id
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse save(@RequestBody @Validated CreateMemberRequest createMemberRequest) {

        Member member = new Member();
        member.setName(createMemberRequest.getName()); // Entity 에서는 setter를 사용 안하지만 이번 강의에서는 편의를 위해 사용

        Long id = memberService.save(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정 API
     *
     * @param id                  Long id
     * @param updateMemberRequest UpdateMemberRequest name (MemberDTO를 사용하지 않고 간단한 예제를 위함)
     * @return UpdateMemberResponse
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse update(@PathVariable("id") Long id, @RequestBody @Validated UpdateMemberRequest updateMemberRequest) {

        memberService.update(id, updateMemberRequest.getName()); // 수정
        Member update = memberService.findOne(id); // 수정된 회원 조회

        return new UpdateMemberResponse(update.getId(), update.getName());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class findMemberResponse {
        private String name;
        private String city;
        private String street;
        private String zipcode;
    }

    @Data
    static class CreateMemberRequest {
        private String name;

        public CreateMemberRequest(String name) {
            this.name = name;
        }
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;

        public UpdateMemberRequest(String name) {
            this.name = name;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
