package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("result")
    public Video video;
    @SerializedName("success")
    public boolean success;
    @SerializedName("error")
    public String error;
}
