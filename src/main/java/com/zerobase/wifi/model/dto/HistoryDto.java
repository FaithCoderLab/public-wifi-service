package com.zerobase.wifi.model.dto;

import com.zerobase.wifi.model.entity.History;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryDto {
    private Long id;
    private double lat;
    private double lnt;
    private String searchDate;

    public static HistoryDto from(History entity) {
        return HistoryDto.builder()
                .id(entity.getId())
                .lat(entity.getLat())
                .lnt(entity.getLnt())
                .searchDate(entity.getSearchDate())
                .build();
    }
}
