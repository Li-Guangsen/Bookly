package com.lgs.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category implements Serializable {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private Integer parentId;//父类别id
    private String description;
    private List<Category> children;
}
