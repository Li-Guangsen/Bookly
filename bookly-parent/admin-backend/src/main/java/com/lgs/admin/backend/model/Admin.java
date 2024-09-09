package com.lgs.admin.backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String note;
    private Integer status;
}
