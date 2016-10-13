package com.beliautopart.bengkel.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private String idOrder;
    private OrderService order;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LinearLayout vewCart;
    private TextView totalCart;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_detail);
        vewCart = (LinearLayout) findViewById(R.id.linierLayoutItemOrder);
        totalCart = (TextView) findViewById(R.id.txtTotalBiaya);
        order.getOrderDetail(idOrder, 1, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        JSONArray arrData = null;
                        try {
                            arrData = new JSONArray(data.getString("listbarang"));
                            Log.d("arrData ",arrData.toString());
                            int Total = 0;
                            for(int i = 0; i < arrData.length(); i++)
                            {
                                JSONObject itemData = arrData.getJSONObject(i);
                                Total += itemData.getInt("qty")*itemData.getInt("harga");
                                addVewCart(
                                        itemData.getString("nama_item"), itemData.getString("kode_item"),
                                        itemData.getInt("qty"), itemData.getInt("harga"),
                                        "http://beliautopart.com/_produk/thumbnail/" + itemData.getString("namafile").replace(" ", "%20")
                                );
                            }
                            totalCart.setText(""+Total);
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
    public void addVewCart(String Nama, String kode, int jumlah, int harga, String foto){

        View child =  this.getLayoutInflater().inflate(R.layout.row_order_cart, null);
        NetworkImageView imgProduk = (NetworkImageView) child.findViewById(R.id.imgProduk);
        TextView txtNama = (TextView) child.findViewById(R.id.txtNamaProduk);
        TextView txtKodeProduk = (TextView) child.findViewById(R.id.txtKodeProduk);
        TextView txtJumlah = (TextView) child.findViewById(R.id.txtJumlah);
        TextView txtHarga = (TextView) child.findViewById(R.id.txtHarga);
        txtNama.setText(Nama);
        txtKodeProduk.setText(kode);
        txtJumlah.setText("jumlah : "+jumlah);
        txtHarga.setText(""+harga);
        imgProduk.setImageUrl(foto,imageLoader);
        vewCart.addView(child);
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
