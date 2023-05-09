package com.springbootjpa.springbootjpa.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookDTO {

    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String author;
    private String isbn;
}
