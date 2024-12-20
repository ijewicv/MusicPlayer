package com.example.musicplayer.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Api;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MusicAdapter;
import com.example.musicplayer.model.MusicInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaylistFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.your_color)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        loadMusicList();
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
                
                requireActivity().runOnUiThread(() -> {
                    musicAdapter = new MusicAdapter(musicList);
                    recyclerView.setAdapter(musicAdapter);
                });
            }
        });
    }
} 