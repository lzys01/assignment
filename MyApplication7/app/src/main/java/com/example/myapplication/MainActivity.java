package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private final static int REQUEST_CODE_RECORD = 1002;
    private RecyclerView mRecyclerview;
    private VideoAdapter mAdapter;
    public List<Video> videoList;
    private String mp4Path = "";
    private FloatingActionsMenu btn_0;
    private com.getbase.floatingactionbutton.FloatingActionButton btn_1;
    private com.getbase.floatingactionbutton.FloatingActionButton btn_2;
    private boolean menuOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        menuOpen = false;
        mRecyclerview = findViewById(R.id.rv);
        mRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new VideoAdapter(this);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemActionListener(new VideoAdapter.OnItemActionListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("videolist", (ArrayList<? extends Parcelable>) videoList);
                bundle.putInt("position",position);
                Intent intent = new Intent(v.getContext(),VideoPlayerActivity.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }

            @Override
            public boolean onItemLongClickListener(View v, int position) {
                return false;
            }
        });
        getData();
        btn_0 = (FloatingActionsMenu)findViewById(R.id.btn_0);
        btn_1 = (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.btn_1);
        btn_1.setOnClickListener(new MenuListener());
        btn_2 = (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.btn_2);
        btn_2.setOnClickListener(new MenuListener());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        try {
            Class<?> clazz = Class . forName ("androidx.appcompat.view.menu.MenuBuilder");
            Method m = clazz . getDeclaredMethod ("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.btn_add:
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                intent.putExtra("is",false);
                startActivity(intent);
                return true;
            case R.id.btn_sat:
                requestPermission();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mp4Path = getOutputMediaPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PathUtils.getUriForFile(this,mp4Path));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent,REQUEST_CODE_RECORD);
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RECORD && resultCode == RESULT_OK){
            Uri videoUri = data.getData();
            Intent intent = new Intent(MainActivity.this,UploadActivity.class);
            intent.putExtra("is",true);
            intent.putExtra("videouri",videoUri);
            MainActivity.this.startActivity(intent);
        }
    }

    public List<Video> getRequest(){
        String urlstring = "https://api-sjtu-camp-2021.bytedance.com/homework/invoke/video";
        List<Video> result = null;
        try{
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.connect();

            if(conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                String s = reader.readLine();
                SocketBean socketBean = new Gson().fromJson(s,SocketBean.class);
                result = socketBean.getFeeds();
                reader.close();
                in.close();
            }
            conn.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                videoList = getRequest();
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(videoList);
                    }
                });
            }
        }).start();
    }

    class MenuListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_1:
                    requestPermission();
                    break;
                case R.id.btn_2:
                    Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                    intent.putExtra("is",false);
                    startActivity(intent);
                    break;
            }
        }
    }
}