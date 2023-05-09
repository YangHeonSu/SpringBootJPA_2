package com.springbootjpa.springbootjpa.domain;


import com.springbootjpa.springbootjpa.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한 테이블에 모두 저장
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 ==//
    public void update(BookDTO bookDTO) {
        this.name = bookDTO.getName();
        this.price = bookDTO.getPrice();
        this.stockQuantity = bookDTO.getStockQuantity();
    }


    /**
     * 재고 수량을 증가
     * @param quantity int quantity
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고 수량 감소
     * @param quantity int quantity
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}
