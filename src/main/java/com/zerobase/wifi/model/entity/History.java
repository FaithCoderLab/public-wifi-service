package com.zerobase.wifi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private Long id;
    private double lat;
    private double lnt;
    private String searchDate;
    private boolean deleteYn;
}
