package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.WebContentService;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private WebContentService webcontent;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        webcontent = new WebContentService(this);
        session = new SessionManager(this);
        webcontent.getSetting(new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if(!resultJson.getBoolean("error")){
                        JSONObject setting = new JSONObject(resultJson.getString("content"));
                        if(session.getSettingMaxBeli()!=setting.getInt("max_pembelian_bengkel"))
                            session.setSettingMaxBeli(setting.getInt("max_pembelian_bengkel"));
                        Log.d("setting",result);

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
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
