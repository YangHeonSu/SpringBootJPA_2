package com.springbootjpa.springbootjpa.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberDTO {
    @NotBlank(message = "이름은 필수 입력값 입니다.")
    private String name;
    private String city;
    private String street;
    private String zipcode;
}
