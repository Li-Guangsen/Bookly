package com.lgs.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Product implements Serializable {
    private Integer id;
    private String name;
    private Integer categoryId;
    private String subtitle;
    private String mainImage;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private BigDecimal price;
    private Integer stock;
    private Integer sale;
    private String detail;
    private Integer status;
    private Integer deleted;
    private Category category;
}
