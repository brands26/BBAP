package com.beliautopart.bengkel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.adapter.JobAdapter;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.RefModel;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.JobService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Animation rotation;
    private Animation rotationC;
    private Animation rotationA;
    private View lA;
    private View lB;
    private View lP;
    private View layoutLoading;
    private BengkelService bengkelService;
    private JobService jobService;
    private Handler handler;
    private SessionManager session;
    private Activity context;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private String idJOb;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker marker;
    private TextView id;
    private TextView nama;
    private TextView jarak;
    private TextView waktu;
    private TextView kendaraan;
    private TextView nopol;
    private TextView kerusakan;
    private RelativeLayout lbAmbilJob;
    private Dialog dialogError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        bengkelService = new BengkelService(this);
        jobService = new JobService(this);
        handler = new Handler();
        session = new SessionManager(this);
        context = (Activity)this;

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert_order_aktif);
        TextView dialogText = (TextView) dialogError.findViewById(R.id.textView27);
        dialogText.setText("Maaf, job ini sudah tidak tersedia lagi.");
        Button dialogErrorButton = (Button) dialogError.findViewById(R.id.btnOk);
        dialogErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialogError.setCancelable(false);

        //imgLoading = (RelativeLayout) findViewById(R.id.imgLoading);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnback.setVisibility(View.VISIBLE);
        txtTitle.setText("Detil Job");
        id = (TextView) findViewById(R.id.txtJobId);
        nama = (TextView) findViewById(R.id.txtNamaCustomer);
        jarak = (TextView) findViewById(R.id.txtJarak);
        waktu = (TextView) findViewById(R.id.txtWaktu);
        kendaraan = (TextView) findViewById(R.id.txtKendaraan);
        nopol = (TextView) findViewById(R.id.txtNopol);
        kerusakan = (TextView) findViewById(R.id.txtKerusakan);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        id.setTypeface(tf);
        Bundle extras = getIntent().getExtras();
        idJOb = extras.getString("id");

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lbAmbilJob = (RelativeLayout) findViewById(R.id.lbAmbilJob);
        lbAmbilJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobService.setAmbilJob(session.getUserId(), idJOb, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject data = new JSONObject(result);
                            if(!data.getBoolean("error")){
                                session.setBengkelAktif(true);
                                session.setBengkelId(idJOb);
                                Intent i = new Intent(context, JobAmbilActivity.class);
                                context.startActivity(i);
                                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                context.finish();
                            }
                            else{
                                if(!dialogError.isShowing())
                                    dialogError.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        };

                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }
        });
        startOnLoadingAnimation();

        Log.d("user+id",session.getUserId()+" "+idJOb);
        jobService.getDetailJobBengkel(idJOb,session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);

                    if(!object.getBoolean("error")){

                        try {
                            JSONObject data = new JSONObject(object.getString("content"));

                            Log.d("id",object.getString("content"));
                            id.setText("Job ID: "+data.getString("id"));
                            nama.setText("Nama Customer: "+data.getString("nama"));
                            jarak.setText("Jarak: "+data.getString("distance"));
                            waktu.setText("Waktu Request: "+data.getString("waktu"));
                            kendaraan.setText("Kendaraan: "+data.getString("nama_kendaraan"));
                            nopol.setText("Nopol: "+data.getString("req_nopol"));
                            if(data.getString("jobcode").equalsIgnoreCase("1"))
                                kerusakan.setText("Kerusakan: "+data.getString("req_description"));
                            else
                                kerusakan.setText("Deskripsi: "+data.getString("req_description"));
                            if(mMap!=null){
                                LatLng lokasiSekarang = new LatLng(data.getDouble("lat_bengkel"),data.getDouble("lng_bengkel"));
                                marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(lokasiSekarang);

                                LatLng lokasi = new LatLng(data.getDouble("lat"), data.getDouble("lng"));
                                marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));
                                builder.include(lokasi);

                                LatLngBounds bounds = builder.build();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                            }
                            stopOnLoadingAnimation();
                        } catch (JSONException e) {
                        }
                    }
                    else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                stopOnLoadingAnimation();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });


    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }

    public void startOnLoadingAnimation(){
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopOnLoadingAnimation(){
        layoutLoading.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-6.2295712, 106.7594778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
    }
}
