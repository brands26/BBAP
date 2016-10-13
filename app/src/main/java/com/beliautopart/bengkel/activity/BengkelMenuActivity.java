package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.utils.GPSTracker;

public class BengkelMenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private GPSTracker gps;
    private SessionManager sesssionmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bengkel_menu);
        sesssionmanager = new SessionManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gps = new GPSTracker(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cari bengkel");
    }
    public void onMotorOnClick(View v){
        if(gps.canGetLocation()){
            Intent i = new Intent(this, BengkelActivityOld.class);
            i.putExtra("jenis", "1");
            startActivity(i);
        }else{
            gps.showSettingsAlert();
        }
    }
    public void onMobilOnClick(View v){
        if(gps.canGetLocation()){
            Intent i = new Intent(this, BengkelActivityOld.class);
            i.putExtra("jenis", "2");
            startActivity(i);
        }else{
            gps.showSettingsAlert();
        }
    }

    public void onAccountClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, AccountActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
    }
    public void onComplaintClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, ComplaintActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
    }
    public void onMyOrderClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, MyOrderActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
    }
    public void onTandCClick(View v) {
        Intent i = new Intent(this, TandCActivity.class);
        startActivity(i);
    }
    public void onPolicyClick(View v) {
        Intent i = new Intent(this, PolicyActivity.class);
        startActivity(i);
    }
    public void onAboutClick(View v) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }
}
