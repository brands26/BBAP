package com.beliautopart.bengkel.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.BengkelService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelVerifikasiBayarJasaFragment extends Fragment {
    private View v;
    private Context context;
    private SessionManager session;
    private BengkelService bengkel;
    private String content;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private ImageButton btnChat;
    private TextView txtStatus;
    private Logic logic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_bengkel_verfikasi_bayar_jasa, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);
        logic = new Logic();

        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel3);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText(session.getBengkelId());
        content = getArguments().getString("content");
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        txtJarak  = (TextView) v.findViewById(R.id.txtJarak);
        txtStatus  = (TextView) v.findViewById(R.id.txtStatus);

        setUp();
        return v;
    }
    public void setUp(){
        try {
            JSONObject data = new JSONObject(content);
            txtNamaBengkel.setText(data.getString("nama"));
            String jarak = data.getString("jarak");
            txtJarak.setText(jarak.substring(0,jarak.length()-10)+"km");
            txtStatus.setText("Anda sudah melakukan konfirmasi pembayaran sebesar Rp "+logic.thousand(""+data.getInt("bill_amount"))+"\nHarap tunggu proses verifikasi pembayaran Anda");

            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
