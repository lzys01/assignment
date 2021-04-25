package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Video implements Parcelable {
    @SerializedName("_id")
    private String Id;
    @SerializedName("student_id")
    private String studentId;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("video_url")
    private String videoUrl;
    @SerializedName("image_w")
    private int imageW;
    @SerializedName("image_h")
    private int imageH;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;
    public void setId(String Id) {
        this.Id = Id;
    }
    public String getId() {
        return Id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getStudentId() {
        return studentId;
    }

    public void setUser_name(String user_name) { this.user_name = user_name; }
    public String getUser_name() {
        return user_name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageUrl() { return imageUrl; }

    public void setVideoUrl(String videoUrl){ this.videoUrl = videoUrl; }
    public String getVideoUrl(){return videoUrl; }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }
    public int getImageW() {
        return imageW;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }
    public int getImageH() {
        return imageH;
    }

    public void setCreatedAt(Date createdat) {
        this.createdAt = createdat;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAtt(Date updatedat) {
        this.updatedAt = updatedat;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(studentId);
        dest.writeString(user_name);
        dest.writeString(imageUrl);
        dest.writeString(videoUrl);
        dest.writeInt(imageH);
        dest.writeInt(imageW);
    }

    public static final Parcelable.Creator<Video> CREATOR  = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            Video video = new Video();
            video.Id = source.readString();
            video.studentId = source.readString();
            video.user_name = source.readString();
            video.imageUrl = source.readString();
            video.videoUrl = source.readString();
            video.imageH = source.readInt();
            video.imageW = source.readInt();
            return video;
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
