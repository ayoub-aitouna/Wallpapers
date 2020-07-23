package com.wallpape.royal.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wallpape.royal.R;
import com.wallpape.royal.about;

public class settings extends Fragment implements View.OnClickListener {
    private View view;
    private LinearLayout privacy, appInfo, rate, other, share, contact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings, container, false);
        setUi();
        setOnclickManagement();
        return view;
    }

    private void setOnclickManagement() {
        privacy.setOnClickListener(this);
        appInfo.setOnClickListener(this);
        rate.setOnClickListener(this);
        other.setOnClickListener(this);
        share.setOnClickListener(this);
        contact.setOnClickListener(this);
    }

    private void setUi() {
        privacy = view.findViewById(R.id.privacyPolicy);
        appInfo = view.findViewById(R.id.about);
        rate = view.findViewById(R.id.rate);
        other = view.findViewById(R.id.other);
        share = view.findViewById(R.id.share);
        contact = view.findViewById(R.id.contact);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:
                startActivity(new Intent(getContext(), about.class));
                break;
            case R.id.privacyPolicy:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://sites.google.com/view/st0reis/accueil"));
                startActivity(i);
                break;
            case R.id.rate:
                Intent review = new Intent(Intent.ACTION_VIEW);
                review.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wallpape.royal"));
                startActivity(review);
                break;
            case R.id.other:
                Intent other = new Intent(Intent.ACTION_VIEW);
                other.setData(Uri.parse("https://play.google.com/store/apps/developer?id=last+fiddler"));
                startActivity(other);
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.wallpape.royal");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;
            case R.id.contact:
                Intent contact = new Intent(Intent.ACTION_VIEW);
                contact.setData(Uri.parse("https://www.facebook.com/ayoub.aitouna.94"));
                startActivity(contact);
                break;
        }
    }
}
