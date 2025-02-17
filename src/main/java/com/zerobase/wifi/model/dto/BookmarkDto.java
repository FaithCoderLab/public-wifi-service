package com.zerobase.wifi.model.dto;

import com.zerobase.wifi.model.entity.Bookmark;
import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.model.entity.Wifi;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkDto {
    private Long id;
    private Long groupId;
    private String groupName;
    private String wifiMgrNo;
    private String wifiName;
    private String regDate;

    public static BookmarkDto from(Bookmark bookmark, BookmarkGroup group, Wifi wifi) {
        return BookmarkDto.builder()
                .id(bookmark.getId())
                .groupId(bookmark.getGroupId())
                .groupName(group.getName())
                .wifiMgrNo(bookmark.getWifiMgrNo())
                .wifiName(wifi.getName())
                .regDate(bookmark.getRegDate())
                .build();
    }

    public static BookmarkDto from(Bookmark bookmark, String groupName, String wifiName) {
        return BookmarkDto.builder()
                .id(bookmark.getId())
                .groupId(bookmark.getGroupId())
                .groupName(groupName)
                .wifiMgrNo(bookmark.getWifiMgrNo())
                .wifiName(wifiName)
                .regDate(bookmark.getRegDate())
                .build();
    }
}
