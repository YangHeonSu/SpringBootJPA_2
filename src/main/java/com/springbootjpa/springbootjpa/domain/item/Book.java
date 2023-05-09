package com.springbootjpa.springbootjpa.domain.item;

import com.springbootjpa.springbootjpa.domain.Item;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")
@Getter @Setter
public class Book extends Item {
    private String author; // 저자
    private String isbn;
}
