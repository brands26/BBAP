package com.beliautopart.bengkel.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.ChatActivity;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.BengkelService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 12/07/2016.
 */
public class JobGetJobFragment extends Fragment {
    private View v;
    private Context context;
    private SessionManager session;
    private BengkelService bengkel;
    private String content;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private RelativeLayout btnChat;
    private String namaBengkel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_job_list, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);


        //ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        //i.setImageResource(R.drawable.stepbengkel1);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtJobRequest);
        tv.setTypeface(tf);

        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText(session.getBengkelId());
        content = getArguments().getString("content");
        setUp();
        return v;
    }
    public void setUp(){
        try {
            JSONObject data = new JSONObject(content);
            txtNamaBengkel.setText(data.getString("nama"));
            namaBengkel= data.getString("nama");
            String jarak = data.getString("jarak");
            txtJarak.setText(jarak.substring(0,jarak.length()-10)+"km");

            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
