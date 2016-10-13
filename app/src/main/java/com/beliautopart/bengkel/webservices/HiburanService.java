package com.beliautopart.bengkel.webservices;

import android.content.Context;

import com.beliautopart.bengkel.app.AppConfig;
import com.beliautopart.bengkel.helper.SendDataHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brandon Pratama on 19/06/2016.
 */
public class HiburanService {
    private final Context context;
    private SendDataHelper sendData;
    private String response;

    public HiburanService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }



    public void Getkategori(SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope","nope");
        sendData.SendData(params, AppConfig.URL_HIBURAN_KATEGORI, 0, callback);
    }
    public void GetFolder(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",id);
        sendData.SendData(params, AppConfig.URL_HIBURAN_FOLDER, 0, callback);
    }
    public void GetKontent(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",id);
        sendData.SendData(params, AppConfig.URL_HIBURAN_KONTENT, 0, callback);
    }
    public void GetDetail(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",id);
        sendData.SendData(params, AppConfig.URL_HIBURAN_DETAIL, 0, callback);
    }


}
