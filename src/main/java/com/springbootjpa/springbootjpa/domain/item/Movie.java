package com.springbootjpa.springbootjpa.domain.item;

import com.springbootjpa.springbootjpa.domain.Item;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M")
@Getter @Setter
public class Movie extends Item {
    private String director;
    private String actor;
}
