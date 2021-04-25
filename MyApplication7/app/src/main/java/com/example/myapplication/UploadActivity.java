package com.example.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "upload";
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_VIDEO = 102;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private Uri videoUri;
    private ImageView coverSD;
    private VideoView videoPre;
    private ImageView play_sign2;
    private Button btn_cover, btn_video;
    private boolean is;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        is = intent.getBooleanExtra("is",false);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        videoPre = findViewById(R.id.video_pre);
        play_sign2 = findViewById(R.id.play_sign2);
        play_sign2.setAlpha(0f);
        btn_cover = findViewById(R.id.btn_cover);
        btn_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });

        btn_video = findViewById(R.id.btn_video);
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideo(REQUEST_CODE_VIDEO,"选择视频");
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        if(is){
            videoUri = intent.getParcelableExtra("videouri");
            Glide.with(coverSD).setDefaultRequestOptions(new RequestOptions().frame(1).centerCrop()).load(videoUri).into(coverSD);
            videoPre.setVideoURI(videoUri);
            play_sign2.setAlpha(1f);
            videoPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoPre.isPlaying()){videoPre.pause();play_sign2.setAlpha(1f);}
                    else {videoPre.start();play_sign2.setAlpha(0f);}
                }
            });
            videoPre.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play_sign2.setAlpha(1f);
                    videoPre.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (videoPre.isPlaying()) {videoPre.pause();play_sign2.setAlpha(1f);}
                            else {videoPre.start();play_sign2.setAlpha(0f);}
                        }
                    });
                }
            });
            btn_video.setOnClickListener(null);
            btn_cover.setOnClickListener(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                Glide.with(this).load(coverImageUri).into(coverSD);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
        if ((REQUEST_CODE_VIDEO == requestCode) && (resultCode == Activity.RESULT_OK)){
            videoUri = data.getData();
            videoPre.setVideoURI(videoUri);
            play_sign2.setAlpha(1f);
            videoPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoPre.isPlaying()){videoPre.pause();play_sign2.setAlpha(1f);}
                    else {videoPre.start();play_sign2.setAlpha(0f);}
                }
            });
            Cursor cursor = getContentResolver().query(videoUri,null,null,null,null);
            cursor.moveToFirst();
            Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            Log.d(TAG,"size="+size.toString());
        }
    }

    private void initNetwork() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void getVideo(int requestCode,String title){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_TITLE,title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        byte[] videoData = readDataFromUri(videoUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面读取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (videoData == null || videoData.length == 0){
            Toast.makeText(this,"视频文件读取失败",Toast.LENGTH_SHORT).show();
        }

        if ( coverImageData.length + videoData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }


        MultipartBody.Part coverPart = MultipartBody.Part.createFormData("cover_image","cover_image.jpg",
                RequestBody.create(MediaType.parse("multipart/form-data"),coverImageData));
        MultipartBody.Part VideoPart = MultipartBody.Part.createFormData("video","video.mp4",
                RequestBody.create(MediaType.parse("multipart/form-data"),videoData));

        Call<UploadResponse> call = api.submitVideo(Constants.STUDENT_ID,
                Constants.USER_NAME,
                "",
                coverPart,
                VideoPart);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.body() != null) {
                    if( response.body().success){
                        finish();
                    }else {
                        Toast.makeText(UploadActivity.this,"提交失败！",Toast.LENGTH_SHORT).show();
                    }
                }

            }


            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(UploadActivity.this,"提交失败！",Toast.LENGTH_SHORT).show();
            }
        });

    }



    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }




}
