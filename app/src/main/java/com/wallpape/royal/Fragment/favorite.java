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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wallpape.royal.Adapters.gallery_adapter;
import com.wallpape.royal.Databse.DatabaseHelper;
import com.wallpape.royal.Model.gallery_item;
import com.wallpape.royal.R;
import com.wallpape.royal.Wallpaper;

import java.util.ArrayList;

public class favorite extends Fragment implements gallery_adapter.MyViewHolder.onmnchurlistener {
    View view;
    private gallery_adapter adapter;
    private ArrayList<gallery_item> data;
    private RecyclerView recyclerView;
    private String Category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.favorite, container, false);
        setUpUi();
        getdata();
        setadapter();
        return view;
    }

    private void getdata() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        data = databaseHelper.getData();
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

    @Override
    public void click(int position) {
        Intent set = new Intent(getActivity(), Wallpaper.class);
        set.putExtra("image", data.get(position).getImage());
        set.putExtra("isFavorite", true);
        startActivity(set);
    }
}
