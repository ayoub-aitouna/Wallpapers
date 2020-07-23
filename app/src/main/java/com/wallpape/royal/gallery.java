package com.wallpape.royal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
import com.wallpape.royal.Model.item_list;

import java.util.ArrayList;

public class gallery extends AppCompatActivity implements gallery_adapter.MyViewHolder.onmnchurlistener {
    private gallery_adapter adapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Wallpapers/Wall");
    private ArrayList<gallery_item> data;
    private RecyclerView recyclerView;
    private String Category;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getData();
        data = new ArrayList<>();
        setUpUi();
        setadapter();
    }

    private void getCategory() {
        Intent get = getIntent();
        Category = get.getStringExtra("category");
        // Toast.makeText(this, "" + Category, Toast.LENGTH_SHORT).show();
    }

    private void setUpUi() {
        recyclerView = findViewById(R.id.recycle);
    }

    private void setadapter() {
        adapter = new gallery_adapter(this, data, this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        getCategory();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    gallery_item Item = item.getValue(gallery_item.class);
                    assert Item != null;
                    if (Item.getName().equals(Category)) {
                        data.add(Item);
                    }
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
        Intent set = new Intent(this, Wallpaper.class);
        set.putExtra("image", data.get(position).getImage());
        startActivity(set);
    }
}
