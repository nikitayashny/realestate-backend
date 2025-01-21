package com.yashny.realestate_backend.dto;

import com.yashny.realestate_backend.entities.Realt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class RealtsResponse {
    private List<Realt> realts;
    private long count;
}
