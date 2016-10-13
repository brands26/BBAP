package com.beliautopart.bengkel.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.adapter.CartAdapter;
import com.beliautopart.bengkel.helper.DialogMap;
import com.beliautopart.bengkel.helper.LocationUser;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.JobService;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    private SessionManager sesssionmanager;
    private Toolbar toolbar;
    private JobService job;
    private TextView txtNama;
    private ImageButton btnCart;
    private TextView countTextview;
    private ImageButton btnHome;
    private Animation rotation;
    private Animation backrotation;
    private Handler handler;
    private TextView countMobilTextview;
    private TextView countMotorTextview;
    private LocationUser locationUser;
    private MediaPlayer notif;
    private JobService jobService;
    private DialogMap dialogMap;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private HomeActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sesssionmanager = new SessionManager(this);
        locationUser = new LocationUser(this);
        jobService = new JobService(this);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.imgLogo);
        logo.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("");
        handler = new Handler();
        job = new JobService(this);
        txtNama = (TextView) findViewById(R.id.txtNama);

        btnHome = (ImageButton) findViewById(R.id.btnHome);
        btnCart = (ImageButton) findViewById(R.id.btnCart);


        countMobilTextview = (TextView) findViewById(R.id.badge_mobil_textView);
        countMotorTextview = (TextView) findViewById(R.id.badge_motor_textView);

        notif = MediaPlayer.create(this, R.raw.notif);

        RelativeLayout buttonPartMobilBackgroun = (RelativeLayout) this.findViewById(R.id.buttonPartMobilBackgroun);
        RelativeLayout buttonPartMotorBackgroun = (RelativeLayout) this.findViewById(R.id.buttonPartMotorBackgroun);
        RelativeLayout buttonBengkelBackgroun = (RelativeLayout) this.findViewById(R.id.buttonBengkelBackgroun);
        RelativeLayout buttonHiburanBackgroun = (RelativeLayout) this.findViewById(R.id.buttonHiburanBackgroun);
        RelativeLayout buttonTentangBackgroun = (RelativeLayout) this.findViewById(R.id.buttonTentangBackgroun);
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation);
        rotation.setRepeatCount(Animation.INFINITE);
        backrotation = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation);
        backrotation.setRepeatCount(Animation.INFINITE);

        buttonPartMobilBackgroun.startAnimation(rotation);
        buttonPartMotorBackgroun.startAnimation(rotation);
        buttonBengkelBackgroun.startAnimation(rotation);
        buttonHiburanBackgroun.startAnimation(rotation);
        buttonTentangBackgroun.startAnimation(backrotation);

        dialogMap = new DialogMap();
        dialogMap.setCancelable(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    if(sesssionmanager.getToken()!=null && !sesssionmanager.getToken().equals(""))
                        mAuth.signInWithCustomToken(sesssionmanager.getToken()).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("tag","signInWithCustomToken:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Log.d("tag", "signInWithCustomToken", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onLoginClick(View v) {
        Intent i = new Intent(this, AuthenticationActivity.class);
        i.putExtra("page", "login");
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onRegisterClick(View v) {
        Intent i = new Intent(this, AuthenticationActivity.class);
        i.putExtra("page", "register");
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onSearchPartMobilClick(View v) {
        if(sesssionmanager.isLoggedIn()){
            Intent i = new Intent(this, JobAmbilActivity.class);
            i.putExtra("bengkel", "2");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            Intent i = new Intent(this, AuthenticationActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    public void onSearchPartMotorClick(View v) {
        if(sesssionmanager.isLoggedIn()){
            Intent i = new Intent(this, JobAmbilActivity.class);
            i.putExtra("bengkel", "1");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            Intent i = new Intent(this, AuthenticationActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
    public void onBengkelClick(View v) {
        if(sesssionmanager.getOrderAktif()){
            Intent i = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getCart() != null && sesssionmanager.isLoggedIn()){
            Intent i = new Intent(this, CartActivity.class);
            i.putExtra("kat", "2");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            Intent i = new Intent(this, SearchActivity.class);
            i.putExtra("kat", "2");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
    public void onAccountClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, AccountActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onComplaintClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, ComplaintActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onMyOrderClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
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
        FragmentManager fm = getSupportFragmentManager();
        Fragment dialogShow = getFragmentManager().findFragmentByTag("lokasi Bengkel");
        if(dialogShow==null && !dialogMap.isVisible())
            dialogMap.show(fm,"lokasi Bengkel");
        /*
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        */
    }
    public void onHiburanClick(View v) {
        Intent i = new Intent(this, NewsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sesssionmanager = new SessionManager(this);

        locationUser = new LocationUser(this);
        locationUser.start();
        String Nama= sesssionmanager.getUserNama();
        if(sesssionmanager.isLoggedIn()){
            txtNama.setText(Html.fromHtml("Selamat Datang, "+"<font color=#3bb1d9><b>"+ Nama +"</b></font>") );
        }
        else{
            txtNama.setText("Selamat Datang");
        }
        if(sesssionmanager.isLoggedIn())
            startGetTime();
    }
    @Override
    protected void onPause(){
        super.onPause();
        stopGetTime();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopGetTime();
    }

    private int countMobilColor = 0;
    private int countMotorColor = 0;
    boolean isRunning = false;
    public Runnable getTimer = new Runnable() {
        @Override
        public void run() {
            job.getTotalJobAvaiable(sesssionmanager.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject resultData = new JSONObject(result);
                        boolean error = resultData.getBoolean("error");
                        if (!error) {
                            JSONObject data = resultData.getJSONObject("content");
                            int motor = data.getInt("motor");
                            int mobil = data.getInt("mobil");
                            if(motor > 0){
                                if(!countMotorTextview.getText().equals(""+motor))
                                    notif.start();
                                countMotorTextview.setVisibility(View.VISIBLE);
                                countMotorTextview.setText(""+motor);
                                if(countMotorColor==1){
                                    countMotorTextview = (TextView) findViewById(R.id.badge_motor_textView);
                                    countMotorTextview.setBackgroundResource(R.drawable.bagde_circle_red);
                                    countMotorTextview.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                                    countMotorColor = 0;
                                }else{
                                    countMotorTextview = (TextView) findViewById(R.id.badge_motor_textView);
                                    countMotorTextview.setBackgroundResource(R.drawable.bagde_circle_yellow);
                                    countMotorTextview.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    countMotorColor = 1;
                                }
                            }
                            else{
                                countMotorTextview.setVisibility(View.GONE);

                            }
                            if(mobil > 0){

                                if(!countMotorTextview.getText().equals(""+mobil))
                                    notif.start();
                                countMobilTextview.setVisibility(View.VISIBLE);
                                countMobilTextview.setText(""+mobil);
                                if(countMobilColor==1){

                                    countMobilTextview = (TextView) findViewById(R.id.badge_mobil_textView);
                                    countMobilTextview.setBackgroundResource(R.drawable.bagde_circle_red);
                                    countMobilTextview.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                                    countMobilColor = 0;
                                }else{
                                    countMobilTextview = (TextView) findViewById(R.id.badge_mobil_textView);
                                    countMobilTextview.setBackgroundResource(R.drawable.bagde_circle_yellow);
                                    countMobilTextview.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    countMobilColor = 1;
                                }
                            }
                            else{
                                countMobilTextview.setVisibility(View.GONE);
                            }
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

            handler.postDelayed(getTimer,500);

        }

    };
    public void startGetTime(){
        if(!isRunning){
            handler.post(getTimer);
            isRunning=true;
        }

    }

    public void stopGetTime(){
        handler.removeCallbacks(getTimer);
        isRunning=false;
    }

}
