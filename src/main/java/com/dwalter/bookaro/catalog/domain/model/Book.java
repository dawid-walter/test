package com.dwalter.bookaro.catalog.domain.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String author;
    private String title;
    private Integer year;
    private BigDecimal price;
    private String coverId;
}
