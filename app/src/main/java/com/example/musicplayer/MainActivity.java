package com.example.musicplayer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.musicplayer.fragment.HomeFragment;
import com.example.musicplayer.fragment.PlaylistFragment;
import com.example.musicplayer.fragment.PlayerFragment;
import com.google.android.material.tabs.TabLayout;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.example.musicplayer.model.MusicInfo;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ImageButton btnList;
    private ImageButton btnPlay;
    private ImageButton btnNext;
    private TextView musicTitle;
    private TextView musicArtist;
    private ImageView musicCover;
    public boolean isPlaying = false;
    private MusicPlayer musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        musicPlayer = MusicPlayer.getInstance();
        musicPlayer.setOnMusicChangeListener(musicInfo -> {
            updatePlayingInfo(musicInfo.getTitle(), musicInfo.getSinger(), musicInfo.getCoverImgUrl());
        });
        
        // 加载音乐列表
        loadMusicList();
        
        // 初始化控件
        initViews();
        
        // 设置播放控制监听器
        setupPlayControls();
        
        // 初始化TabLayout
        tabLayout = findViewById(R.id.tabLayout);
        
        // 默认显示首页Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
        }
        
        // 设置Tab选择监听器
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new HomeFragment();
                        break;
                    case 1:
                        selectedFragment = new PlayerFragment();
                        break;
                    case 2:
                        selectedFragment = new PlaylistFragment();
                        break;
                }
                
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, selectedFragment)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 不需要处理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 不需要处理
            }
        });
        
        // Banner相关代码...
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v. setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Banner网络请求代码...

    }
    
    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        btnList = findViewById(R.id.btnList);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        musicTitle = findViewById(R.id.musicTitle);
        musicArtist = findViewById(R.id.musicArtist);
        musicCover = findViewById(R.id.musicCover);
    }
    
    private void setupPlayControls() {
        btnList.setOnClickListener(v -> {
            tabLayout.getTabAt(2).select();
        });
        
        btnPlay.setOnClickListener(v -> {
            musicPlayer.togglePlay();
            isPlaying = musicPlayer.isPlaying();
            updatePlayButton();
        });
        
        btnNext.setOnClickListener(v -> {
            musicPlayer.playNext();
            isPlaying = true;
            updatePlayButton();
        });

        musicPlayer.setOnPlayStateChangeListener(playing -> {
            isPlaying = playing;
            runOnUiThread(this::updatePlayButton);
        });
    }
    
    public void updatePlayButton() {
        btnPlay.setImageResource(isPlaying ? 
                R.drawable.baseline_pause_24 : 
                R.drawable.baseline_play_arrow_24);
    }
    
    // 更新播放信息的方法
    public void updatePlayingInfo(String title, String artist, String coverUrl) {
        musicTitle.setText(title);
        musicArtist.setText(artist);
        
        Glide.with(this)
                .load(coverUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(musicCover);
    }
    
    private void loadMusicList() {
        new OkHttpClient().newCall(new Request.Builder()
                .url(Api.LIST_URL)
                .get()
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                List<MusicInfo> musicList = new Gson().fromJson(response.body().string(),
                        new TypeToken<List<MusicInfo>>() {}.getType());
                
                runOnUiThread(() -> {
                    musicPlayer.setMusicList(musicList);
                    // 只设置默认音乐信息，不自动播放
                    if (!musicList.isEmpty()) {
                        MusicInfo defaultMusic = musicList.get(0);
                        updatePlayingInfo(defaultMusic.getTitle(), 
                                        defaultMusic.getSinger(), 
                                        defaultMusic.getCoverImgUrl());
                    }
                });
            }
        });
    }
}