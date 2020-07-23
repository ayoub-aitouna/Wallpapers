package com.wallpape.royal;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;
import com.wallpape.royal.Databse.DatabaseHelper;
import com.wallpape.royal.Model.gallery_item;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Wallpaper extends AppCompatActivity implements View.OnClickListener {
    private Button favorite, wall, download;
    private String image;
    private ProgressBar progressBar;
    private ImageView wallpaper;
    public static final int PERMISSION_WRITE = 1;
    private DatabaseHelper databaseHelper;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_WRITE);
            }
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.mInterstitial));
        databaseHelper = new DatabaseHelper(this);
        setUi();
    }
    private void setUi() {
        //get the Category from intent
        Intent get = getIntent();
        image = get.getStringExtra("image");
        boolean isFavorite = get.getBooleanExtra("isFavorite", false);
        // set xml id to variables
        wallpaper = findViewById(R.id.image);
        favorite = findViewById(R.id.favorite);
        progressBar = findViewById(R.id.progress);
        wall = findViewById(R.id.wall);
        download = findViewById(R.id.download);
        //load image from Url
        Picasso.get().load(image).fit().into(wallpaper);
        // set On click management
        wall.setOnClickListener(this);
        download.setOnClickListener(this);
        favorite.setOnClickListener(this);
        //set progress bar to unvisitable
        progressBar.setVisibility(View.GONE);
        //set favorite visibility
        if (checkIfAlreadyInFavoriteList(image)) {
            favorite.setBackgroundResource(R.drawable.ic_favorite_full);
        } else {
            favorite.setBackgroundResource(R.drawable.ic_heart);

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == wall.getId()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_WRITE);
                }
                setWallpaper();
            }
        }
        if (v.getId() == download.getId()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_WRITE);
                } else {
                    DownloadImage();
                }
            } else {
                DownloadImage();
            }
        }
        if (v.getId() == favorite.getId()) {
            if (checkIfAlreadyInFavoriteList(image)) {
                //Toast.makeText(this, "" + checkIfAlreadyInFavoriteList(image), Toast.LENGTH_SHORT).show();
                favorite.setBackgroundResource(R.drawable.ic_heart);
                deleteFromDatabase(image);
            } else {
                favorite.setBackgroundResource(R.drawable.ic_favorite_full);
                //Toast.makeText(this, "" + checkIfAlreadyInFavoriteList(image), Toast.LENGTH_SHORT).show();
                addDataToLocalDatabase();

            }

        }
    }

    private void deleteFromDatabase(String image) {
        databaseHelper.deleteName(image);
    }


    private void addDataToLocalDatabase() {
        databaseHelper.addData(image);
    }

    private void setWallpaper() {
        // Toast.makeText(this, "wallpaper has been Set", Toast.LENGTH_SHORT).show();
        Glide.with(this)
                .asBitmap()
                .load(image)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
                        // wallpaperManager.setBitmap(resource);
                        setWallpaperByDefault(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }


    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_WRITE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "please allow Permission to download ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setWallpaperByDefault(Bitmap bitmap) {
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        Uri myImageUri = getImageUri(getApplicationContext(), bitmap);
        Intent intent = new Intent(myWallpaperManager.getCropAndSetWallpaperIntent(myImageUri));
        startActivity(intent);


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    private void DownloadImage() {
        Glide.with(this)
                .asBitmap()
                .load(image)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        getImageUri(getApplicationContext(), resource);
                        Toast.makeText(
                                getApplicationContext()
                                , "wallpaper has been downloaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        progressBar.setVisibility(View.GONE);
    }

    private boolean checkIfAlreadyInFavoriteList(String url) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList<gallery_item> data = databaseHelper.getData();
        for (gallery_item item : data) {
            if (item.getImage().equals(url)) {

                return true;
            }
        }
        return false;
    }

    public void SaveImage() {
        Bitmap result = ((BitmapDrawable) wallpaper.getDrawable()).getBitmap();
        String Time = new SimpleDateFormat("yyyyMMdd_HHmmss"
                , Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/Wallpaper");
        dir.mkdirs();
        String imageName = Time + ".PNG";
        File file = new File(dir, imageName);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            result.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, "image has been downloaded into DCIM/Wallpaper", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "error while downloading" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);
    }

}
