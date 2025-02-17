package com.zerobase.wifi.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ToPublicWifiInfoDto {
    @SerializedName("list_total_count")
    private int listTotalCount;

    @SerializedName("RESULT")
    private ResultDto result;

    @SerializedName("row")
    private List<WifiDto> row;
}
