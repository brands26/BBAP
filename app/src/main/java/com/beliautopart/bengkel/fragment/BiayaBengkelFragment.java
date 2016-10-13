package com.beliautopart.bengkel.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by brandon on 26/05/16.
 */
public class BiayaBengkelFragment extends Fragment{
    private View v;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private RelativeLayout mapLayout;
    private TextView txtJobID;
    private TextView txtnamaBengkel;
    private TextView txtStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.biaya_bengkel_fragment, container, false);
        txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtnamaBengkel = (TextView) v.findViewById(R.id.txtnamaBengkel);
        txtStatus = (TextView) v.findViewById(R.id.txtNomorOrder);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        return v;
    }
    public void setInfo(String id,String nama,String Status){
        txtJobID.setText(id);
        txtnamaBengkel.setText(nama);
        txtStatus.setText("Bengkel sudah memposting biaya jasa yang Anda sepakati sebesar Rp "+Status+" (rupiah)");
    }
}
