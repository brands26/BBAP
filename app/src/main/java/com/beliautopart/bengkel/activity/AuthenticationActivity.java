package com.beliautopart.bengkel.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.fragment.LoginFragment;
import com.beliautopart.bengkel.fragment.RegisterFragment;

public class AuthenticationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    private double lat= 0;
    private double lng= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.imgLogo);
        logo.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("");
        String page;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                page = "login";
            } else {
                page = extras.getString("page");
            }
        } else {
            page = (String) savedInstanceState.getSerializable("page");
        }
        if (page.equals("login")) {
            loginFragment = new LoginFragment();
            changeFragment(loginFragment);
        } else {
            registerFragment = new RegisterFragment();
            changeFragment(registerFragment);
        }
    }


    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameAuth, fragment);
        fragmentTransaction.commit();
    }
    private void onRegisterButtonClick(View view){
        registerFragment = new RegisterFragment();
        changeFragment(registerFragment);
    }
    public void onBackClick(View v){
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }

    public  void setLocation(double lat, double lng){
        this.lat = lat;
        this.lng = lng;

    }
    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }

}
