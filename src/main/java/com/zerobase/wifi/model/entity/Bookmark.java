package com.zerobase.wifi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    private Long id;
    private Long groupId;
    private String wifiMgrNo;
    private String regDate;
}
