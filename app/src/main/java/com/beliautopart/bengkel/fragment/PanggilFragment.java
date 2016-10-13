package com.beliautopart.bengkel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by brandon on 26/05/16.
 */
public class PanggilFragment extends Fragment implements OnMapReadyCallback {
    private View v;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private RelativeLayout mapLayout;
    private TextView txtJobID;
    private TextView txtnamaBengkel;
    private TextView txtjarakBengkel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.panggil_fragment, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtnamaBengkel = (TextView) v.findViewById(R.id.txtnamaBengkel);
        txtjarakBengkel = (TextView) v.findViewById(R.id.txtjarakBengkel);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void setInfo(String id,String nama,String jarak){
        txtJobID.setText(id);
        txtnamaBengkel.setText(nama);
        txtjarakBengkel.setText(jarak);
    }
}
