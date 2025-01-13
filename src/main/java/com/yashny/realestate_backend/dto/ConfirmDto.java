package com.yashny.realestate_backend.dto;

import lombok.Data;

@Data
public class ConfirmDto {

    private String username;
    private String password;
    private String email;
    private String code;

}
