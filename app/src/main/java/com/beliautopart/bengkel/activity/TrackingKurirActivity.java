package com.beliautopart.bengkel.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.OrderService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brands on 15/08/2016.
 */
public class TrackingKurirActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RelativeLayout layoutLoading;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private Handler handler;
    private OrderService order;
    private SessionManager session;
    private GoogleMap mMap;
    private Marker marker;
    private LatLngBounds.Builder builder;
    private Marker markerKurir;

    private String nama_kurir="";
    private double kurir_lat=0;
    private double kurir_lng=0;
    private double lat_warehouse=0;
    private double lng_warehouse=0;
    private LatLngBounds bounds;
    private double currentLng=0;
    private double currentLat=0;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        handler = new Handler();
        order = new OrderService(this);
        session = new SessionManager(this);

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtTitle.setTypeface(tf);
        btnback.setVisibility(View.VISIBLE);
        txtTitle.setText("Tracking Kurir");
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }



    @Override
    public void onResume(){
        super.onResume();
        startGetDetail();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public Runnable getKurir = new Runnable() {
        @Override
        public void run() {
            order.getKurirLocation(session.getOrderId(), new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            try {
                                JSONObject res = new JSONObject(result);
                                if(!res.getBoolean("error")){
                                    Log.d("ter",result);
                                    JSONObject ocntent = new JSONObject(res.getString("content"));
                                    nama_kurir = ocntent.getString("nama_kurir");
                                    kurir_lat = ocntent.getDouble("kurir_lat");
                                    kurir_lng = ocntent.getDouble("kurir_lng");
                                    lat_warehouse = ocntent.getDouble("lat_warehouse");
                                    lng_warehouse = ocntent.getDouble("lng_warehouse");
                                    setBengkelPosition(kurir_lat,kurir_lng,nama_kurir);
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
            handler.postDelayed(getKurir,1000);

        }

    };
    public void startGetDetail(){
        handler.post(getKurir);
    }
    public void stopGetDetail(){
        handler.removeCallbacks(getKurir);
    }
    public void setBengkelPosition(double lat, double lng,String nama_kurir){
        if(mMap!=null || lat!=0 || lng!=0 || lat!=currentLat || lng!=currentLng ){
            Log.d("tes","tes");
            mMap.clear();
            LatLng lokasi  = new LatLng(lat,lng);
            LatLng lokasiSekarang = new LatLng(session.getUserlat(), session.getUserlng());
            mMap.clear();
            marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
            builder = new LatLngBounds.Builder();
            builder.include(lokasiSekarang);
            markerKurir = mMap.addMarker(new MarkerOptions().position(lokasi).title(nama_kurir).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_kurir)));

            builder.include(lokasi);
            bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,50));
            currentLat=lat;
            currentLng=lng;
        }

    }
}
