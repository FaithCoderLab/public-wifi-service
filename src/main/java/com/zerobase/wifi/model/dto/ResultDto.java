package com.zerobase.wifi.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultDto {
    @SerializedName("CODE")
    private String code;

    @SerializedName("MESSAGE")
    private String message;
}
