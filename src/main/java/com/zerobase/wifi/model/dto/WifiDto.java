package com.zerobase.wifi.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WifiDto {
    @SerializedName("X_SWIFI_MGR_NO")
    private String mgrNo;

    @SerializedName("X_SWIFI_WRDOFC")
    private String district;

    @SerializedName("X_SWIFI_MAIN_NM")
    private String name;

    @SerializedName("X_SWIFI_ADRES1")
    private String roadAddress;

    @SerializedName("X_SWIFI_ADRES2")
    private String detailAddress;

    @SerializedName("X_SWIFI_INSTL_FLOOR")
    private String installFloor;

    @SerializedName("X_SWIFI_INSTL_TY")
    private String installType;

    @SerializedName("X_SWIFI_INSTL_MBY")
    private String installAgency;

    @SerializedName("X_SWIFI_SVC_SE")
    private String serviceType;

    @SerializedName("X_SWIFI_CMCWR")
    private String netType;

    @SerializedName("X_SWIFI_CNSTC_YEAR")
    private String installYear;

    @SerializedName("X_SWIFI_INOUT_DOOR")
    private String inOutDoor;

    @SerializedName("LAT")
    private double lat;

    @SerializedName("LNT")
    private double lnt;

    @SerializedName("WORK_DTTM")
    private String workDate;

    private double distance;
}