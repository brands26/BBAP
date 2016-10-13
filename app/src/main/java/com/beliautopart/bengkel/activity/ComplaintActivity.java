package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.adapter.KomplainAdapterList;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.KomplainModel;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComplaintActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private OrderService order;
    private SessionManager session;
    private RecyclerView recyclerView;
    private List<KomplainModel> komplainList = new ArrayList<>();
    private KomplainAdapterList mAdapter;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout loadindLayout;
    private Animation rotation;
    private Animation rotationA;
    private Animation rotationC;
    private RelativeLayout layoutLoading;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        order = new OrderService(this);
        setContentView(R.layout.activity_complaint);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("List History Complain");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        loadindLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        btnback.setVisibility(View.VISIBLE);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new KomplainAdapterList(this,komplainList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        startOnLoadingAnimation();



        order.getKomplain(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject resultData = new JSONObject(result);
                    boolean error = resultData.getBoolean("error");
                    if (!error) {
                        JSONArray dataArray = resultData.getJSONArray("content");
                        int sizeDataArray = dataArray.length();
                        for (int a = 0; a < sizeDataArray; a++) {
                            JSONObject dataItem = dataArray.getJSONObject(a);
                            KomplainModel komplain = new KomplainModel();
                            komplain.setId(dataItem.getString("orderid"));
                            komplain.setNomor(dataItem.getString("nomor"));
                            komplain.setTanggal(dataItem.getString("waktu"));
                            komplain.setPesan(dataItem.getString("pesan"));
                            komplainList.add(komplain);
                        }
                        mAdapter.notifyDataSetChanged();
                        stopOnLoadingAnimation();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });

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
    public void onAccountClick(View v) {
        Intent i;
        if(session.isLoggedIn())
            i = new Intent(this, AccountActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onComplaintClick(View v) {
        Intent i;
        if(session.isLoggedIn())
            i = new Intent(this, ComplaintActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onMyOrderClick(View v) {
        Intent i;
        if(session.isLoggedIn())
            i = new Intent(this, MyOrderActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onTandCClick(View v) {
        Intent i = new Intent(this, TandCActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onPolicyClick(View v) {
        Intent i = new Intent(this, PolicyActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onAboutClick(View v) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
