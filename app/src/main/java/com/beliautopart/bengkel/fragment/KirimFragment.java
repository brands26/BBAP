package com.beliautopart.bengkel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SessionManager;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

/**
 * Created by brandon on 23/05/16.
 */
public class KirimFragment extends Fragment {

    private View v;
    private RelativeLayout loadingView;
    private TextView txtDiantar;
    private TextView txtNoOrder;
    private CircularProgressView progressView;
    private RelativeLayout diantarView;
    private TextView txtNoOrder2;
    private TextView txtDiantar2;
    private NetworkImageView imgKurir;
    private TextView txtnamaKurir;
    private TextView txtHpKurir;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private RelativeLayout pembayaranView;
    private TextView txtNoOrder3;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_kirim, container, false);
        loadingView = (RelativeLayout) v.findViewById(R.id.loadingLayout);
        diantarView = (RelativeLayout) v.findViewById(R.id.DiatarLayout);
        pembayaranView = (RelativeLayout) v.findViewById(R.id.pembayaranLayout);
        txtNoOrder = (TextView) v.findViewById(R.id.txtNoOrder);
        txtNoOrder2 = (TextView) v.findViewById(R.id.txtNoOrder2);
        txtNoOrder3 = (TextView) v.findViewById(R.id.txtNoOrder3);
        txtDiantar = (TextView) v.findViewById(R.id.txtListDiantar);
        txtDiantar2 = (TextView) v.findViewById(R.id.txtListBarang2);
        txtnamaKurir = (TextView) v.findViewById(R.id.txtNamaKurir);
        txtHpKurir = (TextView) v.findViewById(R.id.txtHpKurir);
        imgKurir = (NetworkImageView) v.findViewById(R.id.imgKurir);
        progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        session = new SessionManager(getContext());
        return v;
    }
    public void stopLoading(){
        loadingView.setVisibility(View.GONE);
    }
    public void setDiantar(String No,String list){
        txtDiantar.setText(list);
        txtNoOrder.setText(No);
        txtNoOrder2.setText(No);
    }
    public void setKurir(String No,String list, String nama, String hp, String img){
        loadingView.setVisibility(View.GONE);
        diantarView.setVisibility(View.GONE);
        txtDiantar2.setText(list);
        txtNoOrder2.setText(No);
        txtNoOrder3.setText(No);
        txtnamaKurir.setText(nama);
        txtHpKurir.setText(hp);
        imgKurir.setImageUrl(img,imageLoader);
    }
    public void setBarangDiterima(String no){
        loadingView.setVisibility(View.GONE);
        diantarView.setVisibility(View.GONE);
        pembayaranView.setVisibility(View.GONE);
        txtNoOrder3.setText(no);
        session.setOrderAktif(false);
        session.emptyCart();

    }
    public void setBarangDiterima(){
        loadingView.setVisibility(View.GONE);
        diantarView.setVisibility(View.GONE);
        pembayaranView.setVisibility(View.GONE);
        session.setOrderAktif(false);
        session.emptyCart();

    }
}
