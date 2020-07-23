package com.wallpape.royal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wallpape.royal.Adapters.home_adapter;
import com.wallpape.royal.Model.item_list;
import com.wallpape.royal.R;
import com.wallpape.royal.gallery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class home extends Fragment implements home_adapter.MyViewHolder.onmnchurlistener {
    private View view;
    private ProgressBar progressBar;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Wallpapers/HeadLine");
    private home_adapter adapter;
    private ArrayList<Object> data;

    // The number of native ads to load.
    private static final int NUMBER_OF_ADS = 2;

    // The AdLoader used to load ads.
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home, container, false);
        MobileAds.initialize(getContext(), getString(R.string.admob_app_id));
        data = new ArrayList<>();
        setUpAdapterAndGetData();
        setUpUi();
        loadNativeAds();
        getData();
        return view;
    }

    private void setUpAdapterAndGetData() {
        adapter = new home_adapter(getContext(), data, this);
    }

    private void setUpUi() {
        progressBar = view.findViewById(R.id.progress);
        //recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setFocusable(false);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.isAutoMeasureEnabled();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    item_list ITEM = item.getValue(item_list.class);
                    assert ITEM != null;
                    ITEM.setId(item.getKey());
                    data.add(ITEM);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (data.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAds) {
            data.add(index, ad);
            index = index + offset;
        }
        Collections.shuffle(data);
        adapter.notifyDataSetChanged();
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(Objects.requireNonNull(getContext()), getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();
        // Load the Native ads.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void click(int position) {
        Intent set = new Intent(getContext(), gallery.class);
        item_list item_list = (item_list) data.get(position);
        set.putExtra("category", item_list.getCategory());
        //Toast.makeText(getContext(), "" + data.get(position).getName(), Toast.LENGTH_SHORT).show();
        startActivity(set);
    }
}
