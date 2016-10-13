package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.JobService;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONException;
import org.json.JSONObject;

public class KomplainActivity extends AppCompatActivity {

    private TextView txtOrderNo;
    private String idOrder;
    private OrderService order;
    private RelativeLayout btnSimpan;
    private ImageButton cartButton;
    private TextView countTextview;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout loadingView;
    private SessionManager session;
    private Toolbar toolbar;
    private EditText inputKomplain;
    private String statusOrder;
    private JobService job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idOrder = "1";
                statusOrder = "1";
            } else {
                idOrder = extras.getString("id");
                statusOrder = extras.getString("status");
            }
        } else {
            idOrder = (String) savedInstanceState.getSerializable("idOrder");
        }
        cartButton = (ImageButton) findViewById(R.id.btnCart);
        inputKomplain = (EditText) findViewById(R.id.inputKomplain);
        countTextview = (TextView) findViewById(R.id.badge_textView);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Komplain Anda");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        loadingView = (RelativeLayout) findViewById(R.id.loadingLayout);
        session = new SessionManager(this);
        job = new JobService(this);
        btnback.setVisibility(View.VISIBLE);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        cartButton.setVisibility(View.GONE);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        order = new OrderService(this);
        txtOrderNo = (TextView) findViewById(R.id.txtKomplainNoOrder);
        txtOrderNo.setTypeface(tf);
        btnSimpan = (RelativeLayout) findViewById(R.id.lbtnSimpan);
        if(statusOrder.equalsIgnoreCase("job")){
            job.getDetailJobBengkel(idOrder,session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getBoolean("error")) {
                            JSONObject data = new JSONObject(obj.getString("content"));
                            txtOrderNo.setText("No. Job: "+data);
                        }
                        else{
                            txtOrderNo.setText("No. Job: ");
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
        else{
            order.getOrderDetail(idOrder, 1, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getBoolean("error")) {
                            JSONObject data = new JSONObject(obj.getString("content"));
                            txtOrderNo.setText("No. Order: "+data.getString("nomor"));
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String komplain = Html.toHtml(inputKomplain.getText());
                order.setKomplain(idOrder, session.getUserId(), "", komplain, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        Toast.makeText(getApplicationContext(),"berhasil ditambahkan",Toast.LENGTH_LONG).show();
                        finish();
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }
        });
    }

    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
