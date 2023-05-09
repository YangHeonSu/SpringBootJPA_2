package com.springbootjpa.springbootjpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    /**
     * 실무에서는 Entity에는 Setter를 사용하는 것을 추천 안함
     * DTO <-> Entity 변환
     * ModelMapper
     * MapStruct
     * Builder
     */
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String name;
    @Embedded // 내장 타입을 의미
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
