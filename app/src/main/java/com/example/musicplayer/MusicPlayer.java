package com.example.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.musicplayer.model.MusicInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private List<MusicInfo> musicList = new ArrayList<>();
    private int currentPosition = -1;
    private OnMusicChangeListener onMusicChangeListener;
    private OnPlayStateChangeListener onPlayStateChangeListener;

    private MusicPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static MusicPlayer getInstance() {
        if (instance == null) {
            synchronized (MusicPlayer.class) {
                if (instance == null) {
                    instance = new MusicPlayer();
                }
            }
        }
        return instance;
    }

    public void setMusicList(List<MusicInfo> list) {
        musicList.clear();
        musicList.addAll(list);
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    public void playMusic(int position) {
        if (position < 0 || position >= musicList.size()) {
            return;
        }

        try {
            currentPosition = position;
            MusicInfo music = musicList.get(position);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getSrc());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (onMusicChangeListener != null) {
                    onMusicChangeListener.onMusicChange(music);
                }
                if (onPlayStateChangeListener != null) {
                    onPlayStateChangeListener.onPlayStateChanged(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playNext() {
        if (currentPosition < musicList.size() - 1) {
            playMusic(currentPosition + 1);
        } else {
            playMusic(0); // 循环播放
        }
    }

    public void togglePlay() {
        if (currentPosition == -1 && !musicList.isEmpty()) {
            playMusic(0);
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (onPlayStateChangeListener != null) {
                onPlayStateChangeListener.onPlayStateChanged(false);
            }
        } else {
            mediaPlayer.start();
            if (onPlayStateChangeListener != null) {
                onPlayStateChangeListener.onPlayStateChanged(true);
            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void setOnMusicChangeListener(OnMusicChangeListener listener) {
        this.onMusicChangeListener = listener;
    }

    public interface OnMusicChangeListener {
        void onMusicChange(MusicInfo musicInfo);
    }

    public MusicInfo getCurrentMusic() {
        if (currentPosition >= 0 && currentPosition < musicList.size()) {
            return musicList.get(currentPosition);
        }
        return null;
    }

    public void setOnPlayStateChangeListener(OnPlayStateChangeListener listener) {
        this.onPlayStateChangeListener = listener;
    }

    public interface OnPlayStateChangeListener {
        void onPlayStateChanged(boolean isPlaying);
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
} 