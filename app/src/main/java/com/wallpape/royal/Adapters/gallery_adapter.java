package com.wallpape.royal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wallpape.royal.Model.gallery_item;
import com.wallpape.royal.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class gallery_adapter extends RecyclerView.Adapter<gallery_adapter.MyViewHolder> {
    private ArrayList<gallery_item> arrayList;
    private LayoutInflater minflater;
    private MyViewHolder.onmnchurlistener monmnchurlistener;
    public Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        onmnchurlistener onmnchurlistener;
        ImageView imageView;

        public MyViewHolder(View v, onmnchurlistener onmnchurlistener) {
            super(v);
            this.onmnchurlistener = onmnchurlistener;
            imageView = v.findViewById(R.id.image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == v.getId()) {
                onmnchurlistener.click(getAdapterPosition());
            }
        }

        public interface onmnchurlistener {
            void click(int position);


        }
    }

    public gallery_adapter(Context context, ArrayList<gallery_item> data, MyViewHolder.onmnchurlistener onmnchurlistener) {
        this.minflater = LayoutInflater.from(context);
        arrayList = data;
        this.context = context;
        this.monmnchurlistener = onmnchurlistener;
    }

    @NotNull
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = minflater.inflate(R.layout.gallery_item, parent, false);

        return new MyViewHolder(view, monmnchurlistener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Picasso.get().load(arrayList.get(position).getImage()).fit().centerCrop().into(holder.imageView);


    }

    public int getItemCount() {
        return arrayList.size();
    }


}

