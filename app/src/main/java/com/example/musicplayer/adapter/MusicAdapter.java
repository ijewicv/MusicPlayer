package com.example.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.R;
import com.example.musicplayer.model.MusicInfo;
import com.example.musicplayer.MusicPlayer;
import com.example.musicplayer.MainActivity;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<MusicInfo> musicList;

    public MusicAdapter(List<MusicInfo> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicInfo musicInfo = musicList.get(position);
        holder.titleText.setText(musicInfo.getTitle());
        holder.singerText.setText(musicInfo.getSinger());
        
        Glide.with(holder.itemView)
                .load(musicInfo.getCoverImgUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.coverImage);
                
        holder.itemView.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) v.getContext();
            MusicPlayer.getInstance().playMusic(position);
            activity.isPlaying = true;
            activity.updatePlayButton();
            activity.updatePlayingInfo(
                musicInfo.getTitle(),
                musicInfo.getSinger(),
                musicInfo.getCoverImgUrl()
            );
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView titleText;
        TextView singerText;

        ViewHolder(View view) {
            super(view);
            coverImage = view.findViewById(R.id.coverImage);
            titleText = view.findViewById(R.id.titleText);
            singerText = view.findViewById(R.id.singerText);
        }
    }
} 