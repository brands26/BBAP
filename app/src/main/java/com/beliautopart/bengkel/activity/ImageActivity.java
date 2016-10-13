package com.beliautopart.bengkel.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.toolbox.ImageLoader;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.utils.NetworkImageView;

public class ImageActivity extends AppCompatActivity {

    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private NetworkImageView image;
    private String gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey("image")) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else
        gambar = extras.getString("image");
        image = (NetworkImageView) findViewById(R.id.fullscreen_image);
        image.setImageUrl(gambar, imageLoader);
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
