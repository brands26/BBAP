package com.beliautopart.bengkel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by brandon on 21/05/16.
 */
public class OrderFragmentOld extends Fragment implements OnMapReadyCallback {
    private View v;
    private CircularProgressView progressView;
    private RelativeLayout loadingView;
    private TextView txtNoOrder;
    private TextView txtNamaWareHouse;
    private TextView txtjarakWareHouse;
    private TextView txtAlamatWareHouse;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker markerwarehouse;
    private Marker markerAnda;
    private LinearLayout vewCart;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private TextView totalCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_old, container, false);
        vewCart = (LinearLayout) v.findViewById(R.id.linierLayoutItemOrder);
        totalCart = (TextView) v.findViewById(R.id.txtTotalBiaya);
        loadingView = (RelativeLayout) v.findViewById(R.id.loadingLayout);
        progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        txtNoOrder = (TextView) v.findViewById(R.id.txtNoOrder);
        txtNamaWareHouse = (TextView) v.findViewById(R.id.txtTimer);
        txtAlamatWareHouse = (TextView) v.findViewById(R.id.txtAlamat);
        txtjarakWareHouse = (TextView) v.findViewById(R.id.txtJarak);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    public void stopAnimation(){
        loadingView.setVisibility(View.GONE);
    }

    public void setStatus(String noOrder,String Nama,String alamat,String jarak,double lat,double lng,double warehouseLat, double warehouseLng,String barang){
        txtNoOrder.setText("Order No. "+noOrder);
        txtNamaWareHouse.setText(Nama);
        txtAlamatWareHouse.setText(alamat);
        txtjarakWareHouse.setText(jarak);

        LatLng lokasiSekarang = new LatLng(lat,lng);
        LatLng warehouse = new LatLng(warehouseLat,warehouseLng);
        markerAnda = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
        markerwarehouse = mMap.addMarker(new MarkerOptions().position(warehouse).title(Nama).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_wh)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(lokasiSekarang);
        builder.include(warehouse);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(barang);
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
    public void addVewCart(String Nama, String kode, int jumlah, int harga, String foto){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_cart, null);
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
}
