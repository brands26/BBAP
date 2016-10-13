package com.beliautopart.bengkel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.activity.JobAmbilActivity;
import com.beliautopart.bengkel.activity.SearchActivity;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelGetResponFragment extends Fragment {
    private String content;
    private Context context;
    private View v;
    private RelativeLayout layoutLoading;
    private SessionManager session;
    private RelativeLayout btnBatal;
    private BengkelService bengkel;
    private Dialog dialogError;
    private TextView txtNamaBengkel;
    private TextView txtStatus;
    private Logic logic;
    private TextView txtInformasi;
    private boolean salon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        salon = ((JobAmbilActivity)getActivity()).getSalon();


        v = inflater.inflate(R.layout.fragment_bengkel_get_respon, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);
        logic = new Logic();
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel1);
        ((JobAmbilActivity)getActivity()).stopOnLoadingAnimation();
        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText("Job ID "+session.getBengkelId());
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNamaBengkel = (TextView) v.findViewById(R.id.txtNamaCustomer);
        txtStatus = (TextView) v.findViewById(R.id.txtStatus);
        txtInformasi = (TextView) v.findViewById(R.id.txtInformasi);
        TextView tv = (TextView) v.findViewById(R.id.txtJobID);
        tv.setTypeface(tf);
        content = getArguments().getString("content");
        setUp(content);
        return v;
    }
    public void setUp(String content){
        try {
            final JSONObject data = new JSONObject(content);
            txtNamaBengkel.setText(data.getString("nama"));

            if(salon){
                txtStatus.setText("Anda telah memposting biaya jasa layanan yang disepakati sebesar "+logic.thousand(""+data.getInt("bill_amount")));
            }
            else{
                txtStatus.setText("Anda telah memposting biaya jasa perbaikan yang disepakati sebesar "+logic.thousand(""+data.getInt("bill_amount")));
            }

            if (data.getString("jobcode").equals("61") || data.getString("jobcode").equals("62") || data.getString("jobcode").equals("66")){
                if(salon){
                    txtStatus.setText("Customer Telah Membayar jasa layanan yang disepakati sebesar Rp"+logic.thousand(""+data.getInt("bill_amount")));
                    txtInformasi.setText(Html.fromHtml("Silakan Anda mulai pekerjaan Anda <b>SEKARANG</b><br/>Jika pekerjaan sudah selesai, mintalah pada Customer untuk mengklik tombol <b>JOB DONE</b> pada layar ponselnya untuk memberi penilaian atas pekerjaan Anda dan mengakhiri job ini"));
                    ((JobAmbilActivity)getActivity()).setStatus("jobSelesai");
                }else{
                    txtStatus.setText("Customer Telah Membayar jasa perbaikan yang disepakati sebesar Rp"+logic.thousand(""+data.getInt("bill_amount")));
                    txtInformasi.setText(Html.fromHtml("Silakan Anda mulai pekerjaan Anda <b>SEKARANG</b><br/>Jika pekerjaan sudah selesai, mintalah pada Customer untuk mengklik tombol <b>JOB DONE</b> pada layar ponselnya untuk memberi penilaian atas pekerjaan Anda dan mengakhiri job ini"));
                    ((JobAmbilActivity)getActivity()).setStatus("jobSelesai");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

}

