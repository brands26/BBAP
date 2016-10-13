package com.beliautopart.bengkel.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
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
import com.beliautopart.bengkel.utils.GPSTracker;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONException;
import org.json.JSONObject;

public class BengkelActivity extends AppCompatActivity {

    private BengkelService bengkelService;
    private OrderService order;
    private Handler handler;
    private SessionManager session;
    private Context context;
    private String status="";
    private BengkelGetResponFragment bengkelGetResponFragment;
    private RelativeLayout layoutLoading;
    private RelativeLayout imgLoading;
    private Animation rotation;
    private GPSTracker gps;
    private Location currentLocation;
    private double currentLatitude;
    private double currentLongitude;
    private BengkelSearchfragment bengkelSearchFragment;
    private BengkelResponseFragment bengkelResponseFragment;
    private BengkelBayarPanggilFragment bengkelBayarPanggilFragment;
    private BengkelVerifikasiBayarPanggilanFragment bengkelVerifikasiBayarPanggilanFragment;
    private BengkelBiayaDiterimaFragment bengkelBiayaDiterimaFragment;
    private BengkelBayarJasaFragment bengkelBayarJasaFragment;
    private BengkelVerifikasiBayarJasaFragment bengkelVerifikasiBayarJasaFragment;
    private BengkelBayarSelesaiFragment bengkelBayarSelesaiFragment;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private Dialog dialogStatus;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private Animation rotationA;
    private Animation rotationC;
    private AlertDialog alertDialog;
    private boolean isActive = false;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bengkel);
        bengkelService = new BengkelService(this);
        session = new SessionManager(this);
        order = new OrderService(this);
        handler = new Handler();
        context = this;

        dialogStatus = new Dialog(context);
        dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStatus.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogStatus.setContentView(R.layout.dialog_status_berubah);
        dialogStatus.setCancelable(true);





        Button btnOk = (Button) dialogStatus.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogStatus.dismiss();
            }
        });

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        //imgLoading = (RelativeLayout) findViewById(R.id.imgLoading);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnback.setVisibility(View.VISIBLE);
        txtTitle.setText("Bengkel");
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
        startOnLoadingAnimation();


    }
    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
    }
    @Override
    public void onResume(){
        super.onResume();
        isActive =true;
        startGetStatusJobOrder();
    }
    @Override
    public void onPause(){
        super.onPause();
        isActive = false;
        stopGetStatusJobOrder();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        isActive =false;
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
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public Runnable orderStatusTread = new Runnable() {
        @Override
        public void run() {
            bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject res = new JSONObject(result);

                        // check for error
                        if (!res.getBoolean("error")) {
                            session.setBengkelAktif(true);
                            JSONObject obj = new JSONObject(res.getString("content"));
                            if(session.getBengkelId().equals(""))
                                session.setBengkelId(obj.getString("jobid"));
                            if (obj.getString("jobcode").equals("19")) {
                                finish();
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();

                            }else if (obj.getString("jobcode").equals("98")) {
                                finish();
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();

                            }else if (obj.getString("jobcode").equals("92")) {
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "ORDER TELAH DI-CANCEL OLEH SISTEM KERENA BATAS WAKTU PEMBAYARAN TELAH LEWAT", Toast.LENGTH_LONG).show();

                            } else if (obj.getString("jobcode").equals("1")) {
                                if(!status.equals("waitResponBengkel")){
                                    txtTitle.setText("Cari Bengkel");
                                    bengkelGetResponFragment = new BengkelGetResponFragment();
                                    changeFragment(bengkelGetResponFragment);
                                    status = "waitResponBengkel";
                                }
                            } else if (obj.getString("jobcode").equals("2")) {
                                if(!status.equals("ResponBengkel")){
                                    dialogStatus();
                                    try {
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        status = "ResponBengkel";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("21")) {
                                if(!status.equals("bayarPanggil")){
                                    dialogStatus();
                                    try {
                                        bengkelBayarPanggilFragment = new BengkelBayarPanggilFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarPanggilFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarPanggilFragment);
                                        status = "bayarPanggil";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("40")) {
                                if(!status.equals("verifiasiPembayaran")){
                                    dialogStatus();
                                    txtTitle.setText("Panggil Bengkel");
                                    try {
                                        bengkelVerifikasiBayarPanggilanFragment = new BengkelVerifikasiBayarPanggilanFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelVerifikasiBayarPanggilanFragment.setArguments(bundle);
                                        changeFragment(bengkelVerifikasiBayarPanggilanFragment);
                                        status = "verifiasiPembayaran";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("41") || obj.getString("jobcode").equals("42")) {
                                if(!status.equals("PembayaranDiterima")){
                                    dialogStatus();
                                    try {
                                        bengkelBiayaDiterimaFragment = new BengkelBiayaDiterimaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBiayaDiterimaFragment.setArguments(bundle);
                                        changeFragment(bengkelBiayaDiterimaFragment);
                                        status = "PembayaranDiterima";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("5")) {
                                if(!status.equals("BayarJasa")){
                                    dialogStatus();
                                    txtTitle.setText("Biaya Perbaikan");
                                    try {
                                        bengkelBayarJasaFragment = new BengkelBayarJasaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarJasaFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarJasaFragment);
                                        status = "BayarJasa";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("60")) {
                                if(!status.equals("VerifikasiBayarJasa")){
                                    dialogStatus();
                                    try {
                                        bengkelVerifikasiBayarJasaFragment = new BengkelVerifikasiBayarJasaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelVerifikasiBayarJasaFragment.setArguments(bundle);
                                        changeFragment(bengkelVerifikasiBayarJasaFragment);
                                        status = "VerifikasiBayarJasa";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("61") || obj.getString("jobcode").equals("62") || obj.getString("jobcode").equals("66")) {
                                if(!status.equals("bayarSelesai")){
                                    dialogStatus();
                                    try {
                                        bengkelBayarSelesaiFragment = new BengkelBayarSelesaiFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarSelesaiFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarSelesaiFragment);
                                        status = "bayarSelesai";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("70")) {
                                if(!status.equals("shipment")){
                                    dialogStatus();
                                }
                            }
                        }
                        else if(res.getBoolean("error")) {
                            if(!status.equals("firsttime") && session.getBengkelId().equals("")){
                                bengkelSearchFragment = new BengkelSearchfragment();
                                changeFragment(bengkelSearchFragment);
                                stopOnLoadingAnimation();
                                stopGetStatusJobOrder();
                                status="firsttime";
                            }
                            if(!session.getBengkelId().equals(""))
                                session.setBengkelAktif(false);
                                session.setBengkelId("");
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
            handler.postDelayed(orderStatusTread,1000);

        }

    };
    public Runnable getTimer = new Runnable() {
        @Override
        public void run() {
            order.getTime(session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if(!obj.getBoolean("error")) {
                            String content = obj.getString("content");
                            //paymentWaitFragment.setTime(content);
                        }
                        else{
                            stopGetTime();
                            startGetStatusJobOrder();
                        }
                        return null;
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
            handler.postDelayed(getTimer,3000);
        }

    };
    public void startGetStatusJobOrder(){
        handler.post(orderStatusTread);
    }
    public void stopGetStatusJobOrder(){
        handler.removeCallbacks(orderStatusTread);
    }
    public void startGetTime(){
        handler.post(getTimer);

    }

    public void stopGetTime(){
        handler.removeCallbacks(getTimer);
    }
    @Override
    public void onStop(){
        super.onStop();
        stopGetStatusJobOrder();
        stopGetTime();
    }
    public void dialogStatus(){
        if(!dialogStatus.isShowing())
            dialogStatus.show();

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}