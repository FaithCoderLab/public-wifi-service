package com.zerobase.wifi.model.entity;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wifi {
    private String mgrNo;
    @Setter
    private double distance;
    private String district;
    private String name;
    private String roadAddress;
    private String detailAddress;
    private String installFloor;
    private String installType;
    private String installAgency;
    private String serviceType;
    private String netType;
    private String installYear;
    private String inOutDoor;
    private double lat;
    private double lnt;
    private String workDate;
}