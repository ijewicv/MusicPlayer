package com.example.musicplayer.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.musicplayer.MusicPlayer;
import com.example.musicplayer.R;
import com.example.musicplayer.model.MusicInfo;

import java.util.Locale;

public class PlayerFragment extends Fragment {
    private ImageView coverImage;
    private TextView titleText;
    private TextView singerText;
    private TextView currentTimeText;
    private TextView totalTimeText;
    private SeekBar seekBar;
    private ObjectAnimator rotationAnimator;
    private float currentRotation = 0f;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (isAdded()) {
                updateProgress();
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        coverImage = view.findViewById(R.id.coverImage);
        titleText = view.findViewById(R.id.titleText);
        singerText = view.findViewById(R.id.singerText);
        currentTimeText = view.findViewById(R.id.currentTimeText);
        totalTimeText = view.findViewById(R.id.totalTimeText);
        seekBar = view.findViewById(R.id.seekBar);
        
        setupSeekBar();
        // 设置旋转动画
        setupRotationAnimation();
        
        // 获取当前播放的音乐信息
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (!musicPlayer.getMusicList().isEmpty()) {
            // 如果有正在播放的音乐，显示当前音乐
            MusicInfo currentMusic = musicPlayer.getCurrentMusic();
            if (currentMusic != null) {
                updateMusicInfo(currentMusic);
            } else {
                // 否则显示列表中的第一首
                updateMusicInfo(musicPlayer.getMusicList().get(0));
            }
        }
        
        // 设置音乐切换监听
        musicPlayer.setOnMusicChangeListener(this::updateMusicInfo);
        
        // 根据播放状态控制动画
        if (musicPlayer.isPlaying()) {
            startRotation();
        } else {
            stopRotation();
        }
        
        // 设置播放状态监听
        musicPlayer.setOnPlayStateChangeListener(isPlaying -> {
            if (isPlaying) {
                startRotation();
            } else {
                stopRotation();
            }
        });
    }
    
    private void setupRotationAnimation() {
        rotationAnimator = ObjectAnimator.ofFloat(coverImage, "rotation", 0f, 360f);
        rotationAnimator.setDuration(20000); // 20秒转一圈
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    }
    
    private void startRotation() {
        if (rotationAnimator != null) {
            // 从当前角度开始旋转
            rotationAnimator.cancel();
            currentRotation = coverImage.getRotation();
            rotationAnimator.setFloatValues(currentRotation, currentRotation + 360f);
            rotationAnimator.start();
        }
    }
    
    private void stopRotation() {
        if (rotationAnimator != null) {
            // 保存当前旋转角度
            currentRotation = coverImage.getRotation();
            rotationAnimator.cancel();
        }
    }
    
    private void updateMusicInfo(MusicInfo musicInfo) {
        if (isAdded()) {  // 确保Fragment已经添加到Activity
            titleText.setText(musicInfo.getTitle());
            singerText.setText(musicInfo.getSinger());
            
            Glide.with(this)
                    .load(musicInfo.getCoverImgUrl())
                    .transform(new CircleCrop())  // 使用圆形裁剪
                    .into(coverImage);
        }
    }
    
    private void setupSeekBar() {
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTimeText.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayer.seekTo(seekBar.getProgress());
                handler.post(updateSeekBar);
            }
        });
    }

    private void updateProgress() {
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (musicPlayer.isPlaying()) {
            int currentPosition = musicPlayer.getCurrentPosition();
            int duration = musicPlayer.getDuration();
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            currentTimeText.setText(formatTime(currentPosition));
            totalTimeText.setText(formatTime(duration));
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateSeekBar);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateSeekBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateSeekBar);
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
            rotationAnimator = null;
        }
    }
} 