package com.yashny.realestate_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class RequestRealtDto {
    private int page;
    private int limit;
    private Long dealTypeId;
    private Long typeId;
    private Long roomsCount;
    private Long maxPrice;
    private Long sortType;
    private Long repair;
    private Long floor;
    private String city;
    private Long minArea;
}
