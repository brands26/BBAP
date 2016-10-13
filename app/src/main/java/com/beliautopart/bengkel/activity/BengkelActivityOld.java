package com.beliautopart.bengkel.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.fragment.BengkelMapFragment;
import com.beliautopart.bengkel.fragment.BiayaBengkelFragment;
import com.beliautopart.bengkel.fragment.DoneFragment;
import com.beliautopart.bengkel.fragment.PanggilFragment;
import com.beliautopart.bengkel.fragment.PerbaikanFragment;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.BankModel;
import com.beliautopart.bengkel.utils.GPSTracker;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BengkelActivityOld extends AppCompatActivity {

    private BengkelMapFragment bengkelMapFragment;
    private BengkelActivityOld context;
    private SessionManager session;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageButton btnNext;
    private int positionTab;
    private String jenis;
    private BengkelService bengkelService;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private ImageButton btnChat;
    private OrderService order;
    private PanggilFragment panggilbengkelFragment;
    private BiayaBengkelFragment biayaBengkelFragment;
    private PerbaikanFragment perbaikanFragment;
    private DoneFragment doneFragment;
    private ProgressDialog pDialog;
    private Location currentLocation;
    private double currentLatitude;
    private double currentLongitude;
    private TextView txtbtnNext;
    private TextView txtbtnChat;
    private ImageButton btnBatal;
    private TextView txtbtnBatal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                jenis = extras.getString("jenis");
            }
            else
                jenis = "1";

        }
        setContentView(R.layout.activity_bengkel1);
        context = this;
        session = new SessionManager(context);
        bengkelService = new BengkelService(this);
        order = new OrderService(this);
        gps = new GPSTracker(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cari Bengkel");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                positionTab=position;
                switch(position){
                    case 0:

                        break;
                    case 1:
                        onPanggilBengkel();
                        break;
                    case 2:
                        onBiayaBengkel();
                    case 3:
                        onPerbaikan();
                        break;
                    case 4:
                        onDone();
                        break;
                }
                for(int a = 0; a<=position;a++){
                    View v = tabLayout.getTabAt(a).getCustomView();
                    TextView tabCount = (TextView) v.findViewById(R.id.tabCount);
                    TextView tabText = (TextView) v.findViewById(R.id.tabText);
                    tabCount.setBackgroundDrawable( getResources().getDrawable(R.drawable.red_circle_bg) );
                    tabText.setTextColor(getResources().getColor(R.color.colorPrimary));
                }


            }
        });
        viewPager.setCurrentItem(0);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnBatal = (ImageButton) findViewById(R.id.btnSimpan);
        txtbtnBatal = (TextView) findViewById(R.id.txtbtnBatal);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("perhatian");
                alertDialog.setMessage("Anda Yakin ingin membatalkannya?");
                alertDialog.setPositiveButton("yakin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        bengkelService.setPembatalanBengkel(session.getBengkelId(), session.getUserId(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                session.setBengkelAktif(false);
                                session.setBengkelUserId("");
                                session.setBengkelId("");
                                finish();
                                Toast.makeText(getApplicationContext(),"Order telah dibatalkan",Toast.LENGTH_LONG).show();
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                        dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }

            });
        btnChat = (ImageButton) findViewById(R.id.btnChat);
        txtbtnChat = (TextView) findViewById(R.id.txtbtnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ChatActivity.class);
                context.startActivity(i);
            }
        });
        btnNext = (ImageButton) findViewById(R.id.btnnext);
        txtbtnNext = (TextView) findViewById(R.id.txtbtnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positionTab==0){
                    if(txtbtnNext.getText().equals("Bayar")){
                        onDialogKonfirmasi();
                    }
                    else if(txtbtnNext.getText().toString().trim().equals("Batal kan")){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("perhatian");
                        alertDialog.setMessage("Anda Yakin ingin membatalkannya?");
                        alertDialog.setPositiveButton("yakin", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bengkelService.setPembatalanBengkel(session.getBengkelId(), session.getUserId(), new SendDataHelper.VolleyCallback() {
                                    @Override
                                    public String onSuccess(String result) {
                                        session.setBengkelAktif(false);
                                        session.setBengkelUserId("");
                                        session.setBengkelId("");
                                        finish();
                                        Toast.makeText(getApplicationContext(),"Order telah dibatalkan",Toast.LENGTH_LONG).show();
                                        return null;
                                    }

                                    @Override
                                    public String onError(VolleyError result) {
                                        return null;
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setNegativeButton("batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                    else{


                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("perhatian");
                        alertDialog.setMessage("Jika lokasi Anda pada peta benar, tekan benar untuk mendapat bantuan bengkel kami");
                        alertDialog.setPositiveButton("benar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*
                                bengkelService.setJobBengkel("1", currentLatitude, currentLongitude, session.getUserId(), jenis, new SendDataHelper.VolleyCallback() {
                                    @Override
                                    public String onSuccess(String result) {
                                        onWaitResponBengkel();
                                        return null;
                                    }

                                    @Override
                                    public String onError(VolleyError result) {
                                        return null;
                                    }
                                });
                                */
                            }
                        });
                        alertDialog.setNegativeButton("salah", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }

                }
                if(positionTab==2){
                    onDialogKonfirmasi();
                }
                if(positionTab==3){
                    if(perbaikanFragment.getStar()==0){
                        Toast.makeText(context,"pastikan memberi rating",Toast.LENGTH_SHORT).show();
                    }
                    else{

                    }
                }
                if(positionTab==4){
                }
            }
        });

        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.d("error :", obj.getString("error"));

                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        session.setBengkelAktif(true);
                        session.setBengkelId(dataOrder.getString("jobid"));
                        if (dataOrder.getString("jobcode").equals("98")){
                            onLoadMap();
                            session.setBengkelAktif(false);
                            finish();
                        }
                        else if (dataOrder.getString("jobcode").equals("2")){
                            onGetResponBengkel();
                        }
                        else if (dataOrder.getString("jobcode").equals("21")){
                            onBayarBengkel();
                        }
                        else if (dataOrder.getString("jobcode").equals("41")||dataOrder.getString("jobcode").equals("42")){
                            viewPager.setCurrentItem(1);
                        }
                        else if (dataOrder.getString("jobcode").equals("5")){
                            viewPager.setCurrentItem(2);
                        }
                        else if (dataOrder.getString("jobcode").equals("61")||dataOrder.getString("jobcode").equals("62")){
                            viewPager.setCurrentItem(3);
                        }
                        else if (dataOrder.getString("jobcode").equals("70")){
                            viewPager.setCurrentItem(4);
                        }
                        else{
                            txtbtnNext.setText("Batal kan");
                            onWaitResponBengkel();
                        }

                    }
                    else{
                        session.setBengkelAktif(false);
                        onLoadMap();
                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }


    private void setupTabIcons() {

        View tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        TextView tabShopingCartCountOne = (TextView) tabOne.findViewById(R.id.tabCount);
        TextView tabhopingCartOne = (TextView) tabOne.findViewById(R.id.tabText);
        tabShopingCartCountOne.setText("1");
        tabhopingCartOne.setText("Cari Bengkel");
        tabShopingCartCountOne.setBackgroundDrawable( getResources().getDrawable(R.drawable.red_circle_bg) );
        tabhopingCartOne.setTextColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.getTabAt(0).setCustomView(tabOne);
        View tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        TextView tabShopingCartCountTwo = (TextView) tabTwo.findViewById(R.id.tabCount);
        TextView tabhopingCartTwo = (TextView) tabTwo.findViewById(R.id.tabText);
        tabShopingCartCountTwo.setText("2");
        tabhopingCartTwo.setText("Panggil");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        View tabthree = LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        TextView tabShopingCartCountthree = (TextView) tabthree.findViewById(R.id.tabCount);
        TextView tabhopingCartthree = (TextView) tabthree.findViewById(R.id.tabText);
        tabShopingCartCountthree.setText("3");
        tabhopingCartthree.setText("Biaya");
        tabLayout.getTabAt(2).setCustomView(tabthree);
        View tab4 = LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        TextView tabShopingCartCount4 = (TextView) tab4.findViewById(R.id.tabCount);
        TextView tabhopingCart4 = (TextView) tab4.findViewById(R.id.tabText);
        tabShopingCartCount4.setText("4");
        tabhopingCart4.setText("Perbaikan");
        tabLayout.getTabAt(3).setCustomView(tab4);
        View tab5 = LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        TextView tabShopingCartCount5 = (TextView) tab5.findViewById(R.id.tabCount);
        TextView tabhopingCart5 = (TextView) tab5.findViewById(R.id.tabText);
        tabShopingCartCount5.setText("5");
        View divider =  tab5.findViewById(R.id.divider);
        divider.setVisibility(View.GONE);
        tabhopingCart5.setText("Done");
        tabLayout.getTabAt(4).setCustomView(tab5);
    }
    private void setupViewPager(ViewPager viewPager) {
        bengkelMapFragment = new BengkelMapFragment();
        panggilbengkelFragment = new PanggilFragment();
        biayaBengkelFragment = new BiayaBengkelFragment();
        perbaikanFragment = new PerbaikanFragment();
        doneFragment = new DoneFragment();
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return bengkelMapFragment;
                    case 1:
                        return panggilbengkelFragment;
                    case 2:
                        return biayaBengkelFragment;
                    case 3:
                        return perbaikanFragment;
                    default:
                        return null;
                }

            }
        };
        viewPager.setAdapter(adapter);
    }

    private void onLoadMap(){
        if(gps.canGetLocation()){
            FindLocation();
        }else{
            gps.showSettingsAlert();
        }
    }

    private void onWaitResponBengkel(){
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.d("error :",obj.getString("error"));
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        session.setBengkelAktif(true);
                        session.setBengkelId(dataOrder.getString("jobid"));
                        bengkelMapFragment.setWaitRespon(dataOrder.getString("jobid"));
                        onLoadingResponBengkel();
                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
    private void onLoadingResponBengkel(){
        bengkelService.getJobDetail(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.d("error :",obj.getString("error"));
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        if(dataOrder.getString("jobcode").equals("2")){
                            viewPager.setCurrentItem(1,true);
                            onGetResponBengkel();

                        }
                        else
                            onLoadingResponBengkel();
                    }
                } catch (JSONException e) {
                    onLoadingResponBengkel();
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                onLoadingResponBengkel();
                return null;
            }
        });
    }
    private void onGetResponBengkel(){
        bengkelService.getJobDetail(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.d("error :",obj.getString("error"));
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));

                        double latSaatini=gps.getLatitude();
                        double lngSaatini=gps.getLatitude();
                        double latBengkel=dataOrder.getDouble("lat");
                        double lngBengkel=dataOrder.getDouble("lng");
                        Location locationA = new Location("point A");

                        locationA.setLatitude(latSaatini);
                        locationA.setLongitude(lngSaatini);

                        Location locationB = new Location("point B");

                        locationB.setLatitude(latBengkel);
                        locationB.setLongitude(lngBengkel);

                        float jarak = locationA.distanceTo(locationB) / 1000;

                        session.setBengkelUserId(dataOrder.getString("uid"));
                        bengkelMapFragment.getOnResponBengkel(dataOrder.getString("jobid"),dataOrder.getString("nama"),jarak,latSaatini,lngSaatini,latBengkel,lngBengkel);
                        onLoadingBayarBengkel();
                    }
                } catch (JSONException e) {
                    onLoadingBayarBengkel();
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                onLoadingBayarBengkel();return null;
            }
        });
        onLoadingBayarBengkel();
    }
    private void onLoadingBayarBengkel(){
        bengkelService.getJobDetail(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.d("error :",obj.getString("error"));
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        if(dataOrder.getString("jobcode").equals("21")){
                            viewPager.setCurrentItem(1,true);
                            onBayarBengkel();

                        }
                        else
                            onLoadingResponBengkel();
                    }
                } catch (JSONException e) {
                    onLoadingBayarBengkel();
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                onLoadingBayarBengkel();
                return null;
            }
        });
    }

    private void onBayarBengkel() {
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            public float jarak;
            public String nama;
            public String jobid;

            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        Log.d("content",obj.getString("content"));
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        double latSaatini=gps.getLatitude();
                        double lngSaatini=gps.getLatitude();
                        double latBengkel=dataOrder.getDouble("gps_lat");
                        double lngBengkel=dataOrder.getDouble("gps_long");
                        Location locationA = new Location("point A");
                        locationA.setLatitude(latSaatini);
                        locationA.setLongitude(lngSaatini);

                        Location locationB = new Location("point B");

                        locationB.setLatitude(latBengkel);
                        locationB.setLongitude(lngBengkel);

                        jarak = locationA.distanceTo(locationB) / 1000;
                        jobid=dataOrder.getString("jobid");
                        nama =dataOrder.getString("nama");
                        order.getRekening(new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.getBoolean("error")) {
                                        bengkelMapFragment.onBayarBengkel(jobid,nama,jarak, obj.getString("content"));
                                        txtbtnNext.setText("Bayar");
                                    }
                                } catch (JSONException e) {
                                }
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });

                        bengkelMapFragment.onBayarBengkel(jobid,nama,jarak, obj.getString("content"));
                        txtbtnNext.setText("Bayar");



                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
    private void onPanggilBengkel() {
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            public float jarak;
            public String nama;
            public String jobid;

            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        Log.d("content",obj.getString("content"));
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        double latSaatini=gps.getLatitude();
                        double lngSaatini=gps.getLatitude();
                        double latBengkel=dataOrder.getDouble("gps_lat");
                        double lngBengkel=dataOrder.getDouble("gps_long");
                        Location locationA = new Location("point A");
                        locationA.setLatitude(latSaatini);
                        locationA.setLongitude(lngSaatini);

                        Location locationB = new Location("point B");

                        locationB.setLatitude(latBengkel);
                        locationB.setLongitude(lngBengkel);

                        jarak = locationA.distanceTo(locationB) / 1000;
                        jobid=dataOrder.getString("jobid");
                        nama =dataOrder.getString("nama");
                        panggilbengkelFragment.setInfo(jobid,nama,""+jarak);
                        txtbtnNext.setText("Next");



                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
        onWaitingBiayaBengkel();
    }

    private void onWaitingBiayaBengkel(){
        bengkelService.getJobDetail(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.d("error :",obj.getString("error"));
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        if(dataOrder.getString("jobcode").equals("5")){
                            viewPager.setCurrentItem(2);
                            onBayarBengkel();

                        }
                        else
                            onWaitingBiayaBengkel();
                    }
                } catch (JSONException e) {
                    onWaitingBiayaBengkel();
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                onWaitingBiayaBengkel();
                return null;
            }
        });
    }
    private void onBiayaBengkel() {
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            public String status;
            public String nama;
            public String jobid;

            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        Log.d("content",obj.getString("content"));
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        jobid=dataOrder.getString("jobid");
                        nama =dataOrder.getString("nama");
                        status =dataOrder.getString("bill_amount");
                        biayaBengkelFragment.setInfo(jobid,nama,status);
                        txtbtnNext.setText("Konfirm Bayar");
                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }

    private void onPerbaikan() {
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            public String status;
            public String nama;
            public String jobid;

            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        Log.d("content",obj.getString("content"));
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        jobid=dataOrder.getString("jobid");
                        nama =dataOrder.getString("nama");
                        status =dataOrder.getString("bill_amount");
                        perbaikanFragment.setInfo(jobid,nama,status);
                        txtbtnNext.setText("Job Done");
                        perbaikanFragment.setLoadingHide();
                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
    private void onDone() {
        bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            public String nama;
            public String jobid;

            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        Log.d("content",obj.getString("content"));
                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                        jobid=dataOrder.getString("jobid");
                        nama =dataOrder.getString("nama");
                        txtbtnNext.setText("Selesai");
                        doneFragment.setInfo(jobid,nama);
                        doneFragment.setLoadingHide();
                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
    private void onDialogKonfirmasi(){
        order.getBank(new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                final List<BankModel> listBank = new ArrayList<BankModel>();
                final List<BankModel> bankPerusahaan = new ArrayList<BankModel>();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject content = new JSONObject(obj.getString("content"));

                        JSONArray arrayBankPerusahaan = new JSONArray(content.getString("bank_perusahaan"));
                        JSONArray arrayListPerusahaan = new JSONArray(content.getString("bank_list"));
                        for(int i = 0; i < arrayBankPerusahaan.length(); i++){
                            JSONObject itemData = arrayBankPerusahaan.getJSONObject(i);
                            BankModel bankModel = new BankModel(itemData.getInt("id_bank"),itemData.getString("nama_bank"));
                            bankPerusahaan.add(bankModel);

                        }
                        for(int i = 0; i < arrayListPerusahaan.length(); i++){
                            JSONObject listData = arrayListPerusahaan.getJSONObject(i);
                            listBank.add(new BankModel(listData.getInt("id"),listData.getString("nama")));
                        }


                    }
                } catch (JSONException e) {
                }

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.setContentView(R.layout.dialog_konfirm_pembayaran);
                dialog.setCancelable(true);
                final Spinner spBankPerusahaan = (Spinner) dialog.findViewById(R.id.spBankPerusaan);
                final Spinner spBankList = (Spinner) dialog.findViewById(R.id.spBankList);
                final EditText inputRekening = (EditText) dialog.findViewById(R.id.inputNomorRekening);
                Button btnKonfirmasi = (Button) dialog.findViewById(R.id.btnKonfirmasi);

                ArrayAdapter<BankModel> dataAdapter = new ArrayAdapter<BankModel>(getBaseContext(),
                        android.R.layout.simple_spinner_item, bankPerusahaan);
                spBankPerusahaan.setAdapter(dataAdapter);
                ArrayAdapter<BankModel> listAdapter = new ArrayAdapter<BankModel>(getBaseContext(),
                        android.R.layout.simple_spinner_item, listBank);
                spBankList.setAdapter(listAdapter);
                btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int bankpengirim;
                        int bankdikirim;
                        BankModel bank1 = listBank.get(spBankList.getSelectedItemPosition());
                        bankpengirim = bank1.getId();
                        BankModel bank2 = bankPerusahaan.get(spBankPerusahaan.getSelectedItemPosition());
                        bankdikirim = bank2.getId();
                        if(txtbtnNext.getText().equals("Konfirm Bayar")){
                            bengkelService.setPembayaranBiaya(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(),bankpengirim,bankdikirim,inputRekening.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                                @Override
                                public String onSuccess(String result) {
                                    viewPager.setCurrentItem(3);
                                    return null;
                                }

                                @Override
                                public String onError(VolleyError result) {
                                    return null;
                                }
                            });
                        }
                        else{
                            bengkelService.setPembayaranJob(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(),bankpengirim,bankdikirim,inputRekening.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                                @Override
                                public String onSuccess(String result) {
                                    Toast.makeText(context,"berhasil",Toast.LENGTH_LONG).show();
                                    return null;
                                }

                                @Override
                                public String onError(VolleyError result) {
                                    return null;
                                }
                            });
                        }
                    }
                });
                dialog.show();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }

    public void FindLocation() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Mendapatkan kordinat saat ini");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }


    void updateLocation(Location location) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if(currentLocation==null){
            currentLocation = location;
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
            Log.d("location saat ini",""+currentLocation);

            bengkelService.getBengkel(jenis, currentLatitude, currentLongitude, "50", new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getBoolean("error")) {
                            bengkelMapFragment.setAllMarker(currentLatitude,currentLongitude,obj.getString("content"));
                        }
                    } catch (JSONException e) {
                    }
                    return null;
                }
                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });
        }

    }
}
