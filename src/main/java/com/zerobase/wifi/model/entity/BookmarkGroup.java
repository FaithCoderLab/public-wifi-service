package com.zerobase.wifi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkGroup {
    private Long id;
    private String name;
    private int orderNo;
    private String regDate;
    private String modDate;
}
