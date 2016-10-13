package com.beliautopart.bengkel.webservices;

import android.content.Context;

import com.beliautopart.bengkel.app.AppConfig;
import com.beliautopart.bengkel.helper.SendDataHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 12/05/16.
 */
public class WebContentService {
    private final Context context;
    private SendDataHelper sendData;
    public WebContentService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }

    public void getAboutUs( SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope", "nope");
        sendData.SendData(params, AppConfig.URL_WEBKONTENT_ABOUT, 0, callback);
    }
    public void getNews( SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope", "nope");
        sendData.SendData(params, AppConfig.URL_WEBKONTENT_NEWS, 0, callback);
    }
    public void getNewsDetail(String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_WEBKONTENT_NEWS_DETAIL, 0, callback);
    }
    public void getSetting( SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope", "nope");
        sendData.SendData(params, AppConfig.URL_WEBKONTENT_SETTING, 0, callback);
    }
}
