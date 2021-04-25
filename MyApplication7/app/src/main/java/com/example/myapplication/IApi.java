package com.example.myapplication;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IApi {
    /**
     *
     * @param student_id 学生id
     * @param user_name 留言学生
     * @param extra_value 额外值
     * @param cover_image 封面图片
     * @param video 视频源文件
     * @return
     */
    @Multipart
    @POST("video")
    Call<UploadResponse> submitVideo(@Query("student_id") String student_id,
                                     @Query("user_name")  String user_name,
                                     @Query("extra_value") String extra_value,
                                     @Part MultipartBody.Part cover_image,
                                     @Part MultipartBody.Part video);
}
