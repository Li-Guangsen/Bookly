package com.lgs.common.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private Integer id;
    private String phone;
    private String password;
    private String icon;
    private String nickname;
    private String note;
    private Integer status;
}
