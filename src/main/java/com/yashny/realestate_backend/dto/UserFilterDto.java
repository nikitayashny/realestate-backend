package com.yashny.realestate_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFilterDto {

    private boolean active;
    private int typeId;
    private int dealTypeId;
    private int roomsCount;
    private int maxPrice;
    private String city;

}
