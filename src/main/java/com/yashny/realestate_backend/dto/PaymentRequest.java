package com.yashny.realestate_backend.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String name;
    private Long price;
    private Long quantity;
}
