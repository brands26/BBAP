package com.beliautopart.bengkel.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.utils.GPSTracker;
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

import java.util.ArrayList;

/**
 * Created by brandon on 25/05/16.
 */
public class BengkelMapFragment extends Fragment implements OnMapReadyCallback {

    private int i = 1;
    private SessionManager session;
    private View v;
    private boolean setuju=false;
    private AlertDialog alertDialog;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GPSTracker gps;
    private Marker marker;
    private LatLng markerLocation;
    private RelativeLayout mapLayout;
    private TextView txtJobID;
    private RelativeLayout waitResponlayout;
    private TextView txtJobIDRespon;
    private TextView txtjarakDariLokasi;
    private TextView txtnamaBengkel;
    private SupportMapFragment mapBengkelFragment;
    private Marker markerBengkel;
    private LatLng lokasiSekarang;
    private LatLng lokasiBengkel;
    private LinearLayout vewRekening;
    private TextView txtJobIDRespon2;
    private TextView txtjarakDariLokasibiaya;
    private TextView txtnamaBengkelbiaya;
    private RelativeLayout responlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.bengkel_map_fragment, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapLayout = (RelativeLayout) v.findViewById(R.id.mapLayout);
        vewRekening = (LinearLayout) v.findViewById(R.id.linierLayoutRekening);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        waitResponlayout = (RelativeLayout) v.findViewById(R.id.WaitResponlayout);
        responlayout = (RelativeLayout) v.findViewById(R.id.ResponLayout);
        txtJobIDRespon = (TextView) v.findViewById(R.id.txtJobIDRespon);
        txtnamaBengkel = (TextView) v.findViewById(R.id.txtnamaBengkel);
        txtjarakDariLokasi = (TextView) v.findViewById(R.id.txtjarakDariLokasi);
        txtJobIDRespon2 = (TextView) v.findViewById(R.id.txtJobIDRespon2);
        txtnamaBengkelbiaya = (TextView) v.findViewById(R.id.txtnamaBengkelbiaya);
        txtjarakDariLokasibiaya = (TextView) v.findViewById(R.id.txtjarakDariLokasibiaya);

        txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void setAllMarker(double lat,double lng,String Array){
        try {
            JSONArray array = new JSONArray(Array);
            if(mMap!=null){
                LatLng lokasiSekarang = new LatLng(lat,lng);
                marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(lokasiSekarang);
                ArrayList<LatLng> locations = new ArrayList();
                for(int a =0; a<array.length();a++){
                    JSONObject object = array.getJSONObject(a);
                    LatLng lokasi = new LatLng(object.getDouble("gps_lat"), object.getDouble("gps_long"));
                    if(object.getString("jnsbengkel").equals("2"))
                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(object.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));
                    else if(object.getString("jnsbengkel").equals("1"))
                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(object.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));
                    locations.add(lokasi);
                }
                for(int a=0;a<locations.size();a++)
                    builder.include(locations.get(a));

                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
            }
        } catch (JSONException e) {
        }
    }
    public void setWaitRespon(String jobId){
        mapLayout.setVisibility(View.GONE);
        txtJobID.setText(jobId);
    }
    public void getOnResponBengkel(String id, String nama, float jarak, double lat, double lng, double bengkellat, double bengkellng ){

        txtJobIDRespon.setText(id);
        txtjarakDariLokasi.setText(""+jarak);
        txtnamaBengkel.setText(nama);
        /*
        lokasiSekarang = new LatLng(lat,lng);
        lokasiBengkel = new LatLng(bengkellat,bengkellng);
        marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Saat ini"));
        markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasiBengkel).title("Lokasi Bengkel"));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(lokasiSekarang);
        builder.include(lokasiBengkel);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
        */
        mapLayout.setVisibility(View.GONE);
        waitResponlayout.setVisibility(View.GONE);
    }
    public void onBayarBengkel(String id, String nama, float jarak, String listRekening){
        txtJobIDRespon2.setText(id);
        txtjarakDariLokasibiaya.setText(""+jarak);
        txtnamaBengkelbiaya.setText(nama);
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(listRekening);
            Log.d("arrData ",arrData.toString());
            for(int i = 0; i < arrData.length(); i++)
            {
                JSONObject data = arrData.getJSONObject(i);
                addVewRekening( data.getInt("id"),data.getString("nama_bank")+" "+data.getString("nama_rek"),data.getString("norek"), data.getString("kantor_cabang"), data.getString("kode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapLayout.setVisibility(View.GONE);
        responlayout.setVisibility(View.GONE);
        waitResponlayout.setVisibility(View.GONE);

    }
    public void addVewRekening(int no,String nama, String noRek, String pt, String kodeTrans){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_rekening, null);
        TextView txtNomer = (TextView) child.findViewById(R.id.txtNomer);
        TextView txtNamaBank = (TextView) child.findViewById(R.id.txtNamaBank);
        TextView txtNoRekening = (TextView) child.findViewById(R.id.txtNoRekening);
        TextView txtPt = (TextView) child.findViewById(R.id.txtPt);
        TextView txtKOdeTransfer = (TextView) child.findViewById(R.id.txtKOdeTransfer);
        txtNomer.setText(""+no);
        txtNamaBank.setText(nama);
        txtNoRekening.setText("No. Rekening: " +noRek);
        txtPt.setText("a/n. "+pt);
        txtKOdeTransfer.setText("Kode Transfer Antar Bank: "+kodeTrans);
        vewRekening.addView(child);
    }
    public LatLng getMapLocation(){
        return markerLocation;
    }
}

