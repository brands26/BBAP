package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailHistoryOrderActivity extends AppCompatActivity {

    private String idOrder;
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
    private LinearLayout viewBiaya;
    private LinearLayout viewOrder;
    private int totalBiaya=0;
    private Logic logic;
    private TextView txtTotalBiaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_order);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idOrder = "1";
            } else {
                idOrder = extras.getString("idOrder");
            }
        } else {
            idOrder = (String) savedInstanceState.getSerializable("idOrder");
        }
        order = new OrderService(this);
        session = new SessionManager(this);
        logic = new Logic();
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

        txtTotalBiaya = (TextView) findViewById(R.id.textView123);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        txtTitle.setText("Detail History Order");
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

        order.getHistoryDetail(idOrder, 0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        JSONArray arrData = null;
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
                                for (int a=0;a<kodeData.length;a++){
                                    Total += (int) Double.parseDouble(qtysData[a])*(int) Double.parseDouble(hargaData[a]);
                                    addVewOrder(
                                            titlesData[a], kodeData[a], hargaData[a],qtysData[a],
                                            "http://beliautopart.com/_produk/thumbnail/" + namafileData[a].replace(" ", "%20")
                                    );
                                }
                                String[] ongkir = itemData.getString("ongkir").split(",");
                                int ongkirSize = ongkir.length;
                                for(int b=0;b<ongkirSize;b++){
                                    int c= b+1;
                                    addVewCart("biaya ongkir " + c, ongkir[b]);
                                }
                            }
                            Total+=data.getInt("orderno");
                            Date time = new Date(Long.parseLong(data.getString("create_date"))* (long) 1000);
                            String vv = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(time);
                            txtTanggal.setText(vv);
                            if(data.getString("nama_jenis").equals("Barang Diterima"))
                                txtStatus.setBackgroundResource(R.drawable.bg_green);
                            else
                                txtStatus.setBackgroundResource(R.drawable.bg_red);
                            txtStatus.setText(data.getString("nama_jenis"));
                            txtOrderNo.setText("Order No. "+data.getString("nomor"));
                            if(!data.getString("orderno").equals("null"))
                                addVewCart("Biaya angka unik.", data.getString("orderno"));

                            txtTotalBiaya.setText("Rp"+logic.thousand(""+totalBiaya));
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
        txtHarga.setText(thousand(harga));
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
        txtHarga.setText(thousand(harga));
        img.setImageUrl(url,imageLoader);
        viewOrder.addView(child);
        addVewCart(Nama+" x "+jumlah,""+Integer.parseInt(jumlah)*Integer.parseInt(harga));
    }
    private String thousand(String number){
        StringBuilder strB = new StringBuilder();
        strB.append(number);
        int Three = 0;

        for(int i=number.length();i>0;i--){
            Three++;
            if(Three == 3){
                strB.insert(i-1, ".");
                Three = 0;
            }
        }
        return strB.toString();
    }
    private String getDate(String time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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

}
