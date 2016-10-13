package com.beliautopart.bengkel.services;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.beliautopart.bengkel.helper.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Brands on 23/08/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private RequestQueue queue;
    private SessionManager session;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token Value: " + refreshedToken);
        session = new SessionManager(getApplicationContext());
        saveToken(refreshedToken);

    }
    private void saveToken(final String token){
        session.setFID(token);
    }
}