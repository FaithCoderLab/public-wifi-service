package com.zerobase.wifi.model.dto;

import com.zerobase.wifi.model.dto.ToPublicWifiInfoDto;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WifiInfoResponse {
    @SerializedName("TbPublicWifiInfo")
    private ToPublicWifiInfoDto toPublicWifiInfo;
}
