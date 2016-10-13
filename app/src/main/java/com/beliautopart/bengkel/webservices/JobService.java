package com.beliautopart.bengkel.webservices;

import android.content.Context;

import com.beliautopart.bengkel.app.AppConfig;
import com.beliautopart.bengkel.helper.SendDataHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brandon Pratama on 12/07/2016.
 */
public class JobService {
    private final Context context;
    private SendDataHelper sendData;
    private String response;

    public JobService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }

    public void getJobAvaiable(String userId,String jenisBengkel, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("jenisBengkel", jenisBengkel);
        sendData.SendData(params, AppConfig.URL_JOB_AVAIABLE, 0, callback);
    }
    public void getTotalJobAvaiable(String userId, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_JOB_TOTAL_AVAIABLE, 0, callback);
    }
    public void getJobStatus(String userId, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_STATUS, 0, callback);
    }
    public void getDetailJobBengkel(String id,String userId, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_DETAIL, 0, callback);
    }
    public void setAmbilJob(String userId,String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("xjobid", id);
        sendData.SendData(params, AppConfig.URL_JOB_AMBIL, 1, callback);
    }
    public void setPosisi(String lat,String lng,String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", lat);
        params.put("lng", lng);
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_JOB_SETPOSISI, 0, callback);
    }
    public void setJobBayarJasa(String userId,String id,String jnsbengkel,String merk,String tipe,String tahun,String nopol,String bill, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("id", id);
        params.put("jnsbengkel", jnsbengkel);
        params.put("merk", merk);
        params.put("tipe", tipe);
        params.put("tahun", tahun);
        params.put("nopol", nopol);
        params.put("bill", bill);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_BAYAR, 1, callback);
    }

    public void setLokasi(String lat,String lng,String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", lat);
        params.put("lng", lng);
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_JOB_SETLOKASI, 0, callback);
    }
}
