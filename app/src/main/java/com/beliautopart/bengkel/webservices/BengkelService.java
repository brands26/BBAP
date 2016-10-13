package com.beliautopart.bengkel.webservices;

import android.content.Context;

import com.beliautopart.bengkel.app.AppConfig;
import com.beliautopart.bengkel.helper.SendDataHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 25/05/16.
 */
public class BengkelService {
    private final Context context;
    private SendDataHelper sendData;
    private String response;

    public BengkelService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }

    public void getBengkel(String id,double lat,double lng,String radius, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("lat",""+lat);
        params.put("lng", ""+lng);
        params.put("radius", radius);
        sendData.SendData(params, AppConfig.URL_BENGKEL_LOCATION, 0, callback);
    }
    public void getJobDetail(String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_DETAIL, 0, callback);
    }
    public void getJobDetailUser(String userId, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_DETAIL_USER, 0, callback);
    }
    public void setJobBengkel(String jobCode,double lat,double lng,String userId,String jenis,String nopol,String kerusakan,String tahunKendaraan,String tipeKendaraan,String merkKendaraan, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", jobCode);
        params.put("lat",""+lat);
        params.put("lng", ""+lng);
        params.put("userId", userId);
        params.put("jenisBengkel", jenis);
        params.put("nopol", nopol);
        params.put("kerusakan", kerusakan);
        params.put("tahunKendaraan", tahunKendaraan);
        params.put("tipeKendaraan", tipeKendaraan);
        params.put("merkKendaraan", merkKendaraan);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB, 1, callback);
    }
    public void getPesan(String id,String destid,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("destid", destid);
        sendData.SendData(params, AppConfig.URL_BENGKEL_READ_CHAT, 0, callback);
    }
    public void setPesan(String id,String userId,String destid,String msg,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userid", userId);
        params.put("destid", destid);
        params.put("msg", msg);
        sendData.SendData(params, AppConfig.URL_BENGKEL_SEND_CHAT, 0, callback);
    }
    public void setImagePesan(String id,String userId,String destid,String filename,String file,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userid", userId);
        params.put("destid", destid);
        params.put("filename", filename);
        params.put("file", file);
        sendData.SendData(params, AppConfig.URL_BENGKEL_SEND_CHAT_IMAGE, 0, callback);
    }
    public void setPembayaranJob(String id,String userId,String benkelId,int bank,int bankperusahaan,String norek,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("benkelId", benkelId);
        params.put("bank",""+ bank);
        params.put("bankperusahaan",""+ bankperusahaan);
        params.put("norek", norek);
        sendData.SendData(params, AppConfig.URL_BENGKEL_SET_PEMBAYARAN, 1, callback);
    }
    public void setPembayaranBiaya(String id,String userId,String benkelId,int bank,int bankperusahaan,String norek,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("benkelId", benkelId);
        params.put("bank",""+ bank);
        params.put("bankperusahaan",""+ bankperusahaan);
        params.put("norek", norek);
        sendData.SendData(params, AppConfig.URL_BENGKEL_SET_PEMBAYARAN_BIAYA, 1, callback);
    }
    public void setPembatalanBengkel(String id,String userId,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_BENGKEL_BATAL, 1, callback);
    }
    public void setReadyBayar(String id,String userId,String benkelId,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("benkelId", benkelId);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_READY_BAYAR, 0, callback);
    }
    public void setDone(String id,String userId,String benkelId,String rating,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("benkelId", benkelId);
        params.put("rating", rating);
        sendData.SendData(params, AppConfig.URL_BENGKEL_JOB_DONE, 0, callback);
    }
    public void setPembatalanJob(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_BENGKEL_BATAL, 0, callback);
    }


}
