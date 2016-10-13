package com.beliautopart.bengkel.activity;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.adapter.JobAdapter;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.fragment.BengkelBayarJasaFragment;
import com.beliautopart.bengkel.fragment.BengkelBayarPanggilFragment;
import com.beliautopart.bengkel.fragment.BengkelBayarSelesaiFragment;
import com.beliautopart.bengkel.fragment.BengkelBiayaDiterimaFragment;
import com.beliautopart.bengkel.fragment.BengkelGetResponFragment;
import com.beliautopart.bengkel.fragment.BengkelResponseFragment;
import com.beliautopart.bengkel.fragment.BengkelSearchfragment;
import com.beliautopart.bengkel.fragment.BengkelVerifikasiBayarJasaFragment;
import com.beliautopart.bengkel.fragment.BengkelVerifikasiBayarPanggilanFragment;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.JobModel;
import com.beliautopart.bengkel.model.KomplainModel;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.JobService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JobActivity extends AppCompatActivity {

    private Animation rotation;
    private Animation rotationA;
    private Animation rotationC;
    private View lB;
    private View lA;
    private View lP;
    private RelativeLayout layoutLoading;

    private BengkelService bengkelService;
    private Handler handler;
    private SessionManager session;
    private Activity context;
    private String status="";
    private JobService jobService;
    private RecyclerView recyclerView;
    private List<JobModel> jobList = new ArrayList<>();
    private JobAdapter mAdapter;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout Ltidaktersedia;

    String jenisBengkel;
    private RelativeLayout btnMobil;
    private RelativeLayout btnMotor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        bengkelService = new BengkelService(this);
        jobService = new JobService(this);
        handler = new Handler();
        session = new SessionManager(this);
        context = (Activity)this;
        setContentView(R.layout.activity_job);
        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        Ltidaktersedia = (RelativeLayout) findViewById(R.id.Ltidaktersedia);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        //imgLoading = (RelativeLayout) findViewById(R.id.imgLoading);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            jenisBengkel = "1";
            job(true);
        } else {
            jenisBengkel = extras.getString("bengkel");
            job(true);
        }


        btnMobil = (RelativeLayout) findViewById(R.id.relativeLayout29);
        btnMotor = (RelativeLayout) findViewById(R.id.relativeLayout31);
        btnMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisBengkel="2";
                job(true);
            }
        });
        btnMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisBengkel="1";
                job(true);
            }
        });

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnback.setVisibility(View.VISIBLE);
        txtTitle.setText("Job Request");
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new JobAdapter(jobList,context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        startOnLoadingAnimation();
    }


    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        startcekJobTread();
    }
    @Override
    public void onPause(){
        super.onPause();
        stopcekJobTread();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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

    public Runnable cekJobTread = new Runnable() {
        @Override
        public void run() {
            job(false);
            handler.postDelayed(cekJobTread,1000);

        }

    };

    public void job(final boolean button){
        if(button){
            stopcekJobTread();
            startOnLoadingAnimation();
        }
        jobService.getJobAvaiable(session.getUserId(),jenisBengkel, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    Log.d("result",result);
                    stopOnLoadingAnimation();
                    JSONObject res = new JSONObject(result);
                    // check for error
                    if (!res.getBoolean("error")) {
                        Ltidaktersedia.setVisibility(View.GONE);
                        session.setBengkelAktif(true);
                        JSONArray dataArray = res.getJSONArray("content");
                        int sizeDataArray = dataArray.length();
                        jobList.clear();
                        for (int a = 0; a < sizeDataArray; a++) {
                            JSONObject dataItem = dataArray.getJSONObject(a);
                            JobModel jobModel = new JobModel();
                            jobModel.setId(dataItem.getString("id"));
                            jobModel.setNama("Nama Customer: "+dataItem.getString("nama"));
                            jobModel.setMerk("Merk: "+dataItem.getString("nama_kendaraan"));
                            jobModel.setTipe("Tipe: "+dataItem.getString("nama_tipe"));
                            jobModel.setDistance("Jarak: "+dataItem.getString("distance"));
                            jobModel.setWaktu("Waktu Request: "+dataItem.getString("waktu"));
                            jobModel.setNopol("Nopol: "+dataItem.getString("req_nopol"));
                            jobModel.setUserId(dataItem.getString("uid0"));
                            if(dataItem.getString("jobcode").equalsIgnoreCase("1")){
                                jobModel.setJenis("Jenis Job: Service");
                                jobModel.setDescription("kerusakan: "+dataItem.getString("req_description"));
                            }
                            else{
                                jobModel.setJenis("Jenis Job: Salon");
                                jobModel.setDescription("Deskripsi: "+dataItem.getString("req_description"));

                            }
                            jobList.add(jobModel);
                        }
                        mAdapter.notifyDataSetChanged();

                    }
                    else if(res.getBoolean("error")) {
                        Ltidaktersedia.setVisibility(View.VISIBLE);
                    }

                    if(button){
                        startcekJobTread();
                        stopOnLoadingAnimation();
                    }
                } catch (JSONException e) {
                }
                return result;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });

    }
    public void startcekJobTread(){
        handler.post(cekJobTread);
        AppController.getInstance().cancelPendingRequests("volley");
    }
    public void stopcekJobTread(){
        handler.removeCallbacks(cekJobTread);
    }
}
