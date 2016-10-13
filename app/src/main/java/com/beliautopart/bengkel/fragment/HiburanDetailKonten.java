package com.beliautopart.bengkel.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.NewsActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Brandon Pratama on 19/06/2016.
 */
public class HiburanDetailKonten extends Fragment{
    private View v;
    private Activity context;
    private OrderService orderService;
    private SessionManager session;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private RelativeLayout btnBatal;
    private String nama;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LinearLayout vewList;
    private String url;
    private TextView txtJudul;
    private TextView txtIsi;
    private TextView txtTgl;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_hiburan_detail_content, container, false);
        context = (Activity)getContext();
        orderService = new OrderService(context);
        session = new SessionManager(context);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        //txtTitle.setText("Order");
        txtJudul =(TextView) v.findViewById(R.id.txtJudul);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtJudul.setTypeface(tf);
        txtIsi =(TextView) v.findViewById(R.id.txtIsi);
        txtTgl =(TextView) v.findViewById(R.id.txtTgl);
        content = getArguments().getString("content");
        setUp(content);
        return v;
    }
    public void setUp(String content){
        if(!content.equals("")){
            try {
                JSONObject obj = new JSONObject(content);
                txtIsi.setText(Html.fromHtml(obj.getString("isi")));
                txtJudul.setText(obj.getString("judul"));
                Date time = new java.util.Date(Long.parseLong(obj.getString("tgl"))* (long) 1000);
                String vv = new SimpleDateFormat("EEE, d MMM yyyy").format(time);
                txtTgl.setText(vv);
                ((NewsActivity)getActivity()).stopOnLoadingAnimation();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            ((NewsActivity)getActivity()).stopOnLoadingAnimation();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
