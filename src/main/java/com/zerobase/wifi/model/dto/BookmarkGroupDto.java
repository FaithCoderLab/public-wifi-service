package com.zerobase.wifi.model.dto;

import com.zerobase.wifi.model.entity.BookmarkGroup;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkGroupDto {
    private Long id;
    private String name;
    private int orderNo;
    private String regDate;
    private String modDate;

    public static BookmarkGroupDto from(BookmarkGroup entity) {
        return BookmarkGroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .orderNo(entity.getOrderNo())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }
}
