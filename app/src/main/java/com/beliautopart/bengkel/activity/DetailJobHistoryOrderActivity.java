package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.OrderService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Brands on 13/09/2016.
 */
public class DetailJobHistoryOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String idJob;
    private OrderService order;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LinearLayout vewCart;
    private TextView totalCart;
    private TextView txtOrderNo;
    private ImageButton cartButton;
    private TextView countTextview;
    private ImageButton btnback;
    private TextView txtTitle;
    private Toolbar toolbar;
    private TextView txtTanggal;
    private TextView txtStatus;
    private SessionManager session;
    private RelativeLayout layoutLoading;
    private RelativeLayout lB;
    private RelativeLayout lP;
    private RelativeLayout lA;
    private Animation rotation;
    private Animation rotationA;
    private Animation rotationC;
    private TextView txtNamaKendaraan;
    private TextView txtNopol;
    private TextView txtTahun;
    private TextView txtDeskripsi;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker marker;
    private RatingBar ratingBengkel;
    private TextView txtNamaBengkel;
    private TextView txtKedatangan;
    private LinearLayout viewBiaya;
    private int totalBiaya=0;
    private TextView txtTotalBiaya;
    private Logic logic;
    private LinearLayout viewOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_job);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idJob = "1";
            } else {
                idJob = extras.getString("idJob");
            }
        } else {
            idJob = (String) savedInstanceState.getSerializable("idJob");
        }
        order = new OrderService(this);
        session = new SessionManager(this);
        logic = new Logic();
        vewCart = (LinearLayout) findViewById(R.id.linierLayoutItemOrder);
        viewBiaya =(LinearLayout) findViewById(R.id.linierBiaya);
        viewOrder =(LinearLayout) findViewById(R.id.linierOrder);
        totalCart = (TextView) findViewById(R.id.txtTotalBiaya);
        txtOrderNo = (TextView) findViewById(R.id.txtOrderNo);
        txtTanggal = (TextView) findViewById(R.id.txtTanggal);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        cartButton = (ImageButton) findViewById(R.id.btnCart);
        countTextview = (TextView) findViewById(R.id.badge_textView);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtNamaKendaraan = (TextView) findViewById(R.id.textView119);
        txtNopol = (TextView) findViewById(R.id.textView110);
        txtTahun = (TextView) findViewById(R.id.textView111);
        txtDeskripsi = (TextView) findViewById(R.id.textView112);
        txtNamaBengkel = (TextView) findViewById(R.id.textView119b);
        txtKedatangan = (TextView) findViewById(R.id.textView122);
        ratingBengkel = (RatingBar) findViewById(R.id.ratingBar2);
        txtTotalBiaya = (TextView) findViewById(R.id.textView123);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        txtTitle.setText("Detail History JOB");
        txtOrderNo.setTypeface(tf);
        btnback.setVisibility(View.VISIBLE);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);
        startOnLoadingAnimation();

        order.getHistoryJobDetail(idJob, 0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        JSONArray arrData = null;
                        try {


                        /*
                            arrData = new JSONArray(data.getString("listbarangMulti"));
                            int Total = 0;
                            for(int i = 0; i < arrData.length(); i++)
                            {
                                JSONObject itemData = arrData.getJSONObject(i);
                                String[] titlesData =itemData.getString("titles").split(",");
                                String[] qtysData =itemData.getString("qtys").split(",");
                                String[] hargaData =itemData.getString("harga").split(",");
                                String[] namafileData =itemData.getString("namafile").split(",");
                                String[] kodeData =itemData.getString("kode_item").split(",");
                                for (int a=0;a<kodeData.length;a++){
                                    Total += (int) Double.parseDouble(qtysData[a])*(int) Double.parseDouble(hargaData[a]);
                                    addVewCart(
                                            titlesData[a], kodeData[a],
                                            (int) Double.parseDouble(qtysData[a]), (int) Double.parseDouble(hargaData[a]),
                                            "http://beliautopart.com/_produk/thumbnail/" + namafileData[a].replace(" ", "%20")
                                    );
                                }
                            }

                            Total+=data.getInt("orderno");

                        */
                            if(!data.getString("waktu").equals("null")){
                                Date time = new Date(Long.parseLong(data.getString("waktu"))* (long) 1000);
                                String vv = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(time);
                                txtTanggal.setText(vv);
                            }
                            if(data.getString("nama_jenis").equalsIgnoreCase("job done"))
                                txtStatus.setBackgroundResource(R.drawable.bg_green);
                            else
                                txtStatus.setBackgroundResource(R.drawable.bg_red);
                            txtStatus.setText(data.getString("nama_jenis"));
                            //totalCart.setText("Rp"+thousand(""+Total));
                            txtOrderNo.setText(data.getString("nomor"));
                            txtNamaKendaraan.setText("Kendaraan: "+data.getString("namaKendaraan"));
                            txtNopol.setText("Plat Nomor: "+data.getString("req_nopol"));
                            if(data.getString("req_tahun").equalsIgnoreCase("null") || data.getString("req_tahun")==null || data.getString("req_tahun").equalsIgnoreCase("semua")){
                                txtTahun.setText("Tahun: -");
                            }
                            else{
                                txtTahun.setText("Tahun: "+data.getString("req_tahun"));
                            }
                            txtDeskripsi.setText("Deskripsi: "+data.getString("req_description"));
                            final LatLng lokasiSekarang = new LatLng(data.getDouble("lat"),data.getDouble("lng"));
                            marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasiSekarang, 15));
                                }
                            });

                            if(!data.getString("listbarangMulti").equalsIgnoreCase("null")){
                                try {
                                    arrData = new JSONArray(data.getString("listbarangMulti"));
                                    int Total = 0;
                                    for(int i = 0; i < arrData.length(); i++)
                                    {
                                        JSONObject itemData = arrData.getJSONObject(i);
                                        String[] titlesData =itemData.getString("titles").split(",");
                                        String[] qtysData =itemData.getString("qtys").split(",");
                                        String[] hargaData =itemData.getString("harga").split(",");
                                        String[] namafileData =itemData.getString("namafile").split(",");
                                        String[] kodeData =itemData.getString("kode_item").split(",");
                                        for(int a=0;a<kodeData.length;a++)
                                            if(!kodeData[a].contentEquals("JOB"))
                                                addVewOrder(titlesData[a], kodeData[a],hargaData[a],qtysData[a],"http://beliautopart.com/_produk/thumbnail/" + namafileData[a].replace(" ", "%20"));
                                        String[] ongkir = itemData.getString("ongkir").split(",");
                                        int ongkirSize = ongkir.length;
                                        for(int b=0;b<ongkirSize;b++){
                                            int c= b+1;
                                            addVewCart("biaya ongkir " + c, ongkir[b]);
                                        }
                                    }
                                    stopOnLoadingAnimation();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{

                            }
                            if(!data.getString("rating").equals("null"))
                                ratingBengkel.setRating(data.getInt("rating"));
                            txtNamaBengkel.setText("Nama Customer: "+data.getString("nama"));
                            txtKedatangan.setText("Waktu Kedatangan: "+getTime(data.getString("waktu")));
                            String[] biaya = data.getString("biaya").split(",");
                            if(!biaya[0].equals("null"))
                                if(biaya.length==1){
                                    addVewCart("Biaya panggilan.", biaya[0]);
                                }
                                else{
                                    addVewCart("Biaya panggilan.", biaya[0]);
                                    if(data.getString("statusjob").equals("101"))
                                        addVewCart("Biaya jasa Salon.", biaya[1]);
                                    else
                                        addVewCart("Biaya jasa Bengkel.", biaya[1]);
                                }
                            if(!data.getString("orderno").equals("null"))
                                addVewCart("Biaya angka unik.", data.getString("orderno"));

                            txtTotalBiaya.setText("Rp"+logic.thousand(""+totalBiaya));

                            if(data.getString("statusjob").equals("101")){
                                TextView textView120 = (TextView) findViewById(R.id.textView120);
                                textView120.setText("Rating Salon:");
                                TextView textView119 = (TextView) findViewById(R.id.textView199);
                                textView119.setText("Lokasi:");
                                CardView card_view_order = (CardView) findViewById(R.id.card_view_order);
                                card_view_order.setVisibility(View.GONE);
                                txtNamaBengkel.setText("Salon: "+data.getString("nama"));
                            }
                            stopOnLoadingAnimation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
    public void startOnLoadingAnimation(){
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopOnLoadingAnimation(){
        layoutLoading.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }
    public void addVewCart(String Nama, String harga){

        View child =  this.getLayoutInflater().inflate(R.layout.row_rincian_biaya, null);
        TextView txtNama = (TextView) child.findViewById(R.id.textView124);
        TextView txtHarga = (TextView) child.findViewById(R.id.textView125);
        txtNama.setText(Nama);
        txtHarga.setText(logic.thousand(harga));
        viewBiaya.addView(child);
        totalBiaya+=Integer.parseInt(harga);
    }
    public void addVewOrder(String Nama, String kode,String harga,String jumlah,String url){

        View child =  this.getLayoutInflater().inflate(R.layout.row_rincian_order_parts, null);
        NetworkImageView img= (NetworkImageView) child.findViewById(R.id.imgProduk);
        TextView txtNama = (TextView) child.findViewById(R.id.txtNamaProduk);
        TextView txtKodeProduk = (TextView) child.findViewById(R.id.txtKodeProduk);
        TextView txtJumlah = (TextView) child.findViewById(R.id.txtJumlah);
        TextView txtHarga = (TextView) child.findViewById(R.id.txtHarga);
        txtNama.setText(Nama);
        txtKodeProduk.setText(kode);
        txtJumlah.setText("Jumlah:"+jumlah);
        txtHarga.setText(logic.thousand(harga));
        img.setImageUrl(url,imageLoader);
        viewOrder.addView(child);
        addVewCart(Nama+" x "+jumlah,""+Integer.parseInt(jumlah)*Integer.parseInt(harga));
    }
    private String getTime(String waktu) {
        Date time = new Date(Long.parseLong(waktu)* (long) 1000);
        String vv = new SimpleDateFormat("HH:mm").format(time);
        return vv;
    }
    public void onAccountClick(View v) {
        Intent i;
        if(session.isLoggedIn())
            i = new Intent(this, AccountActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onComplaintClick(View v) {
        Intent i;
        if(session.isLoggedIn())
            i = new Intent(this, ComplaintActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onMyOrderClick(View v) {
        Intent i;
        if(session.isLoggedIn())
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
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
