package com.beliautopart.bengkel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by brandon on 26/05/16.
 */
public class PerbaikanFragment extends Fragment {
    private View v;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private RelativeLayout mapLayout;
    private TextView txtJobID;
    private TextView txtnamaBengkel;
    private TextView txtStatus;
    private CircularProgressView progressView;
    private RelativeLayout loadingView;
    private RatingBar rt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.perbaikan_bengkel_fragment, container, false);
        txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtnamaBengkel = (TextView) v.findViewById(R.id.txtnamaBengkel);
        loadingView = (RelativeLayout) v.findViewById(R.id.loadingLayout);
        progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        txtStatus = (TextView) v.findViewById(R.id.txtNomorOrder);
        rt = (RatingBar) v.findViewById(R.id.ratingBar);
        return v;
    }
    public void setLoadingHide(){
        loadingView.setVisibility(View.GONE);
    }
    public void setInfo(String id,String nama,String Status){
        txtJobID.setText(id);
        txtnamaBengkel.setText(nama);
        txtStatus.setText("Bengkel sudah memposting biaya jasa yang Anda sepakati sebesar Rp "+Status+" (rupiah)");
    }
    public int getStar(){
        return rt.getNumStars();
    }

}
