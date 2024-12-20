package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.Api;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.RecAdapter;
import com.example.musicplayer.model.BannerInfo;
import com.example.musicplayer.model.RecInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private Banner banner;
    private RecyclerView recyclerView;
    private RecAdapter recAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        banner = view.findViewById(R.id.banner);
        recyclerView = view.findViewById(R.id.recyclerView);
        
        // 设置网格布局管理器，3列
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        
        // 加载Banner数据
        loadBannerData();
        
        // 加载推荐数据
        loadRecData();
    }
    
    private void loadBannerData() {
        new OkHttpClient().newCall(new Request.Builder()
                .url(Api.BANNER_URL)
                .get()
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                List<BannerInfo> lists = new Gson().fromJson(response.body().string(), new TypeToken<>() {
                });
                requireActivity().runOnUiThread(() -> {
                    banner.setAdapter(new BannerImageAdapter<BannerInfo>(lists) {
                                @Override
                                public void onBindView(BannerImageHolder holder, BannerInfo data, int position, int size) {
                                    Glide.with(holder.itemView)
                                            .load(data.getBannerImgUrl())
                                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                                            .into(holder.imageView);
                                }
                            })
                            .addBannerLifecycleObserver(requireActivity())
                            .setIndicator(new CircleIndicator(requireActivity()));
                });
            }
        });
    }
    
    private void loadRecData() {
        new OkHttpClient().newCall(new Request.Builder()
                .url(Api.REC_URL)
                .get()
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                List<RecInfo> lists = new Gson().fromJson(response.body().string(), 
                        new TypeToken<List<RecInfo>>() {}.getType());
                
                requireActivity().runOnUiThread(() -> {
                    recAdapter = new RecAdapter(lists);
                    recyclerView.setAdapter(recAdapter);
                });
            }
        });
    }
}