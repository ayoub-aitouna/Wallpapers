package com.wallpape.royal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wallpape.royal.Adapters.gallery_adapter;
import com.wallpape.royal.Model.gallery_item;
import com.wallpape.royal.R;
import com.wallpape.royal.Wallpaper;

import java.util.ArrayList;

public class recent extends Fragment implements gallery_adapter.MyViewHolder.onmnchurlistener {
    View view;
    private gallery_adapter adapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Wallpapers/Wall");
    private ArrayList<gallery_item> data;
    private RecyclerView recyclerView;
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recent, container, false);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setUpUi();
        setadapter();
        getData();
        return view;

    }

    private void setUpUi() {
        data = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycle);
    }

    private void setadapter() {
        adapter = new gallery_adapter(getContext(), data, this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        // Read from the database

        myRef.limitToFirst(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    gallery_item Item = item.getValue(gallery_item.class);
                    assert Item != null;
                    data.add(Item);
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

    @Override
    public void click(int position) {
        Intent set = new Intent(getActivity(), Wallpaper.class);
        set.putExtra("image", data.get(position).getImage());
        startActivity(set);
    }
}
