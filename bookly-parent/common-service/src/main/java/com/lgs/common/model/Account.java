package com.lgs.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {
    private String username;
    private String password;
    private String captcha;
}
