package com.beliautopart.bengkel.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by brandon on 12/05/16.
 */
public class SendDataHelper {
    private Activity context;
    private ProgressDialog pDialog;
    private Dialog dialogError;
    private String url;

    public SendDataHelper(Context context) {
        this.context = (Activity) context;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        dialogError = new Dialog(context);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);
    }


    public void SendData(final Map<String, String> params, final String url, final int dialog, final VolleyCallback callback) {


        Log.d("param : ",""+params);
        if (dialog == 1) {
            showDialog();
        }
        this.url = url;

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                if (error instanceof TimeoutError) {
                    Log.e("Volley ", "Login Error: " + "Waktu Habis");
                } else if (error instanceof AuthFailureError) {
                    Log.e("Volley ", "AuthFailureError " + error);
                } else if (error instanceof ServerError) {
                    Log.e("Volley ", "AuthFailureError " + error);
                } else if (error instanceof NetworkError) {
                    Log.e("Volley ", "NetworkError " + error);
                } else if (error instanceof ParseError) {
                    Log.e("Volley ", "ParseError " + error);
                } else if (error instanceof NoConnectionError) {
                    Log.e("Volley ", "ParseError " + error);
                }
                showDoneAlert(params,url,dialog,callback);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(6000000, 0, 0));


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "volley");
    }





    public interface VolleyCallback {
        String onSuccess(String result);

        String onError(VolleyError result);
    }

    private void showDialog() {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    public void showDoneAlert(final Map<String, String> params, final String url, final int dialog, final VolleyCallback callback){
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData(params,url,dialog,callback);
                dialogError.dismiss();
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                dialogError.dismiss();
            }
        });
        if (dialogError != null && !dialogError.isShowing())
            dialogError.show();
    }

}
