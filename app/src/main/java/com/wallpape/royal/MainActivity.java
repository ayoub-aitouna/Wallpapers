package com.wallpape.royal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.wallpape.royal.Databse.DatabaseHelper;
import com.wallpape.royal.Fragment.favorite;
import com.wallpape.royal.Fragment.home;
import com.wallpape.royal.Fragment.recent;
import com.wallpape.royal.Fragment.settings;
import com.wallpape.royal.Model.gallery_item;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ChipNavigationBar chipNavigationBar;
    private FrameLayout frameLayout;
    private FragmentManager fragmentManager;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Wallpapers");
    private ArrayList<gallery_item> recentData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chipNavigationBar = findViewById(R.id.chip_navigation);
        frameLayout = findViewById(R.id.framlayout);
        if (savedInstanceState == null) {
            chipNavigationBar.setItemSelected(R.id.home, true);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.framlayout, new home()).commit();
        }
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.home:
                        fragment = new home();
                        break;
                    case R.id.recent:
                        fragment = new recent();
                        break;
                    case R.id.favorites:
                        fragment = new favorite();
                        break;
                    case R.id.settings:
                        fragment = new settings();
                        break;
                }
                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.framlayout, fragment).commit();
                } else {
                    Log.e(TAG, "Error");
                }
            }
        });
        setBadget();
    }

    private void setBadget() {
        DatabaseHelper data = new DatabaseHelper(this);
        ArrayList<gallery_item> Data = data.getData();
        if (Data.size() < 99)
            chipNavigationBar.showBadge(R.id.favorites, Data.size());
        else chipNavigationBar.showBadge(R.id.favorites, 99);

        myRef.limitToFirst(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    gallery_item Item = item.getValue(gallery_item.class);
                    assert Item != null;
                    recentData.add(Item);
                }
                int count = recentData.size();
                if (count < 99)
                    chipNavigationBar.showBadge(R.id.recent, recentData.size());
                else chipNavigationBar.showBadge(R.id.recent, 99);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


}
