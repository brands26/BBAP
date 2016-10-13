package com.beliautopart.bengkel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.beliautopart.bengkel.helper.ProgressBarAnimation;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.MessageModel;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.JobService;
import com.beliautopart.bengkel.webservices.OrderService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONException;
import org.json.JSONObject;

public class JobAmbilActivity extends AppCompatActivity {

    private BengkelService bengkelService;
    private SessionManager session;
    private OrderService order;
    private Handler handler;
    private JobAmbilActivity context;
    private RelativeLayout layoutLoading;
    private RelativeLayout lA;
    private RelativeLayout lB;
    private RelativeLayout lP;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private Animation rotation;
    private Animation rotationA;
    private Animation rotationC;
    private JobService jobService;
    private String jenisBengkel;
    private MediaPlayer notif;
    private Dialog dialogChat;
    private String namaBengkel;
    private FirebaseDatabase mDatabase;
    private Query myRef;
    private ChildEventListener childEventListener;
    private RelativeLayout card_view_progress;
    private TextView progressText;
    private ProgressBar progressBar2;
    private ProgressBarAnimation anim;
    private int pro=0;
    private boolean salon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_ambil);
        bengkelService = new BengkelService(this);
        jobService = new JobService(this);
        session = new SessionManager(this);
        order = new OrderService(this);
        handler = new Handler();
        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            jenisBengkel = "1";
        } else {
            jenisBengkel = extras.getString("bengkel");
        }

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        //imgLoading = (RelativeLayout) findViewById(R.id.imgLoading);

        dialogChat = new Dialog(context);
        dialogChat.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChat.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogChat.setContentView(R.layout.dialog_alert);
        dialogChat.setCancelable(false);
        TextView txtMessage = (TextView) dialogChat.findViewById(R.id.txtMessage);
        txtMessage.setText("Anda mendapatkan pesan dari Customer, baca sekarang?");
        Button btnCobaLagi = (Button) dialogChat.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Baca");
        Button btnBatal = (Button) dialogChat.findViewById(R.id.btnBatal);
        btnBatal.setText("Nanti");
        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("nama",namaBengkel);
                context.startActivity(i);
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                dialogChat.dismiss();
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogChat.dismiss();
            }
        });

        notif = MediaPlayer.create(this, R.raw.notif);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnback.setVisibility(View.VISIBLE);
        if(salon)
            txtTitle.setText("Job Salon");
        else
            txtTitle.setText("Job Bengkel");
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
        card_view_progress = (RelativeLayout) findViewById(R.id.relativeLayout12);
        progressText = (TextView) findViewById(R.id.textView106);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.setProgress(0);

        mDatabase = FirebaseDatabase.getInstance();

        myRef = mDatabase.getReference("jobs").orderByChild("id").equalTo(session.getBengkelId());
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildChanged:" + dataSnapshot.child("messages").getValue());
                Log.d("tag", "onChildAdded:" + dataSnapshot.child("messages").getValue());
                long total = dataSnapshot.child("messages").getChildrenCount();
                long tot = 0;
                for (DataSnapshot messageSnapshot: dataSnapshot.child("messages").getChildren()) {
                    tot=tot+1;
                    Log.d("total ="+total,"saat ini"+tot);
                    String userId = (String) messageSnapshot.child("id").getValue();
                    boolean is_read = (boolean) messageSnapshot.child("is_read").getValue();
                    if(!userId.contentEquals(session.getUserId())){
                        mDatabase.getReference("jobs").child(dataSnapshot.getKey()).child("messages").child(messageSnapshot.getKey()).child("is_sent").setValue(true);
                    }
                    if(!userId.contentEquals(session.getUserId()) && !is_read && tot==total){
                        notif.start();
                        if (dialogChat != null && !dialogChat.isShowing())
                            dialogChat.show();
                    }
                }


                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildChanged:" + dataSnapshot.child("messages").getValue());
                Log.d("tag", "onChildAdded:" + dataSnapshot.child("messages").getValue());
                long total = dataSnapshot.child("messages").getChildrenCount();
                long tot = 0;
                for (DataSnapshot messageSnapshot: dataSnapshot.child("messages").getChildren()) {
                    tot=tot+1;
                    Log.d("total ="+total,"saat ini"+tot);
                    String userId = (String) messageSnapshot.child("id").getValue();
                    boolean is_read = (boolean) messageSnapshot.child("is_read").getValue();
                    if(!userId.contentEquals(session.getUserId())){
                        mDatabase.getReference("jobs").child(dataSnapshot.getKey()).child("messages").child(messageSnapshot.getKey()).child("is_sent").setValue(true);
                    }
                    if(!userId.contentEquals(session.getUserId()) && !is_read && tot==total){
                        notif.start();
                        if (dialogChat != null && !dialogChat.isShowing())
                            dialogChat.show();
                    }
                }
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("tag", "onChildRemoved:" + dataSnapshot.child("messages").getValue());


                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildMoved:" + dataSnapshot.child("messages").getValue());


                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("tag", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "gagal mendapatkan info chat.",
                        Toast.LENGTH_SHORT).show();
            }
        };

    }
    @Override
    public void onStart(){
        super.onStart();
        myRef.addChildEventListener(childEventListener);
    }
    @Override
    public void onResume(){
        super.onResume();
        startGetStatusJobOrder();
    }
    @Override
    public void onPause(){
        super.onPause();
        myRef.removeEventListener(childEventListener);
        stopGetStatusJobOrder();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        myRef.removeEventListener(childEventListener);
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
        fragmentTransaction.commitAllowingStateLoss();
    }

    private String status = "";
    private int gagal = 0;
    private BengkelResponseFragment bengkelResponseFragment;
    private BengkelGetResponFragment bengkelGetResponFragment;


    public Runnable getTimer = new Runnable() {
        @Override
        public void run() {
            jobService.setPosisi(""+session.getUserlat(),""+session.getUserlng(),session.getBengkelId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if(!obj.getBoolean("error")) {
                            bengkelResponseFragment.changeMap(session.getUserlat(),session.getUserlng());
                            //Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
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
    public void startSetPosisi(){
        handler.post(getTimer);

    }

    public void stopSetPosisi(){
        handler.removeCallbacks(getTimer);
    }

    public Runnable orderStatusTread = new Runnable() {
        @Override
        public void run() {
            jobService.getJobStatus(session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject res = new JSONObject(result);

                        // check for error
                        if (!res.getBoolean("error")) {
                            session.setBengkelAktif(true);
                            JSONObject obj = new JSONObject(res.getString("content"));
                            if(session.getBengkelId().equals("")||session.getBengkelId()==null){
                                session.setBengkelId(obj.getString("jobid"));
                            }
                            else{
                                namaBengkel = obj.getString("nama");
                                session.setNamaUserJob(namaBengkel);
                            }
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
                                    card_view_progress.setVisibility(View.VISIBLE);
                                    setProgress(20,"Langkah 1 dari 5");
                                    txtTitle.setText("Cari Bengkel");
                                    bengkelResponseFragment = new BengkelResponseFragment();
                                    changeFragment(bengkelResponseFragment);
                                    status = "waitResponBengkel";
                                }
                            } else if (obj.getString("jobcode").equals("2")) {
                                if(!status.equals("ResponBengkel")){
                                    try {
                                        setProgress(20,"Langkah 1 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkel";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("102")) {
                                if(!status.equals("ResponSalon")){
                                    setSalonAktif();
                                    try {
                                        setProgress(20,"Langkah 1 dari 5");
                                        txtTitle.setText("Job Salon");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponSalon";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("21")) {
                                if(!status.equals("ResponBengkel")){
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkel";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("121")) {
                                if(!status.equals("ResponBengkel")){
                                    setSalonAktif();
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkel";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("4")) {
                                if(!status.equals("ResponBengkelDatang")){
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkelDatang";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("104")) {
                                if(!status.equals("ResponBengkelDatang")){
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkelDatang";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("40")) {
                                if(!status.equals("ResponBengkelA")){
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkelA";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("140")) {
                                if(!status.equals("ResponBengkelA")){
                                    setSalonAktif();
                                    Log.d("jobcode",obj.getString("jobcode"));
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkelA";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("41") || obj.getString("jobcode").equals("42")) {
                                if(!status.equals("ResponBengkelB")){
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkelB";
                                        startSetPosisi();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("141") || obj.getString("jobcode").equals("142")) {
                                if(!status.equals("ResponBengkelB")){
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        stopOnLoadingAnimation();
                                        status = "ResponBengkelB";
                                        startSetPosisi();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("5")) {
                                if(!status.equals("ResponBiayaJasaA")){
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        stopSetPosisi();
                                        bengkelGetResponFragment = new BengkelGetResponFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelGetResponFragment.setArguments(bundle);
                                        changeFragment(bengkelGetResponFragment);
                                        status = "ResponBiayaJasaA";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("105")) {
                                if(!status.equals("ResponBiayaJasaA")){
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        stopSetPosisi();
                                        bengkelGetResponFragment = new BengkelGetResponFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelGetResponFragment.setArguments(bundle);
                                        changeFragment(bengkelGetResponFragment);
                                        status = "ResponBiayaJasaA";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("60")) {
                                if(!status.equals("ResponBiayaJasaA")){
                                    try {
                                        setProgress(80,"Langkah 4 dari 5");
                                        bengkelGetResponFragment = new BengkelGetResponFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelGetResponFragment.setArguments(bundle);
                                        changeFragment(bengkelGetResponFragment);
                                        status = "ResponBiayaJasaA";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("160")) {
                                if(!status.equals("ResponBiayaJasaA")){
                                    try {
                                        setProgress(80,"Langkah 4 dari 5");
                                        bengkelGetResponFragment = new BengkelGetResponFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelGetResponFragment.setArguments(bundle);
                                        changeFragment(bengkelGetResponFragment);
                                        status = "ResponBiayaJasaA";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("61") || obj.getString("jobcode").equals("62") || obj.getString("jobcode").equals("66")) {
                                if(!status.equals("ResponBiayaJasaB")){
                                    try {
                                        setProgress(100,"Selesai");
                                        bengkelGetResponFragment = new BengkelGetResponFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelGetResponFragment.setArguments(bundle);
                                        changeFragment(bengkelGetResponFragment);
                                        status = "ResponBiayaJasaB";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("161") || obj.getString("jobcode").equals("162") || obj.getString("jobcode").equals("66")) {
                                if(!status.equals("ResponBiayaJasaB")){
                                    try {
                                        setProgress(100,"Selesai");
                                        bengkelGetResponFragment = new BengkelGetResponFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelGetResponFragment.setArguments(bundle);
                                        changeFragment(bengkelGetResponFragment);
                                        status = "ResponBiayaJasaB";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        else if(res.getBoolean("error")) {
                            if(status.equals("jobSelesai")){
                                showDoneAlert();

                            }
                            else if(gagal==3){
                                Intent i = new Intent(context, JobActivity.class);
                                i.putExtra("bengkel",jenisBengkel);
                                context.startActivity(i);
                                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                context.finish();
                            }
                            gagal++;
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
    public void startGetStatusJobOrder(){
        handler.post(orderStatusTread);
    }
    public void stopGetStatusJobOrder(){
        handler.removeCallbacks(orderStatusTread);
    }
    @Override
    public void onStop(){
        super.onStop();
        stopGetStatusJobOrder();
    }
    public void showDoneAlert(){
        stopGetStatusJobOrder();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_bengkel_selesai);
        dialog.setCancelable(false);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setBengkelAktif(false);
                session.setBengkelId("");
                session.setBengkelUserId("");
                session.setOrderAktif(false);
                session.setOrderId("");
                Intent i = new Intent(context, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void setProgress(int now,String text){
        card_view_progress.setVisibility(View.VISIBLE);
        anim = new ProgressBarAnimation(progressBar2, pro, now);
        anim.setDuration(1000);
        progressText.setText(text);
        progressBar2.startAnimation(anim);
        pro=pro+20;
    }

    public void setStatus(String status){
        this.status=status;
    }
    public void setSalonAktif(){
        salon = true;
        session.setBengkelSalon(true);
    }
    public boolean getSalon(){
        return salon;
    }
}