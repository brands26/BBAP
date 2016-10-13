package com.beliautopart.bengkel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.BengkelService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelBayarSelesaiFragment extends Fragment{
    private View v;
    private Context context;
    private SessionManager session;
    private BengkelService bengkel;
    private String content;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private ImageButton btnChat;
    private TextView txtStatus;
    private RatingBar rtgBar;
    private TextView txtRating;
    private RelativeLayout btnDone;
    private Logic logic;
    private RelativeLayout btnOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_bengkel_bayar_selesai, container, false);
        context = getContext();
        logic = new Logic();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel5);

        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText(session.getBengkelId());
        content = getArguments().getString("content");
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        txtStatus  = (TextView) v.findViewById(R.id.txtStatus);
        txtRating  = (TextView) v.findViewById(R.id.txtRating);
        btnDone = (RelativeLayout) v.findViewById(R.id.lbtnChat);
        btnOrder = (RelativeLayout) v.findViewById(R.id.lbtnOrder);
        rtgBar = (RatingBar) v.findViewById(R.id.rtgBar);
        rtgBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                if(rating==1)
                    txtRating.setText("Sangat Mengecewakan");
                else if(rating==2)
                    txtRating.setText("Mengecewakan");
                else if(rating==3)
                    txtRating.setText("Biasa");
                else if(rating==4)
                    txtRating.setText("Memuaskan");
                else if(rating==5)
                    txtRating.setText("Sangat Memuaskan");

            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rtgBar.getRating()<1){
                    Toast.makeText(context,"Anda harus memberikan rating terhadap pelayanan bengkel!",Toast.LENGTH_SHORT).show();
                }
                else{
                    bengkel.setDone(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(), ""+rtgBar.getRating(), new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            session.setBengkelAktif(false);
                            session.setBengkelUserId("");
                            session.setBengkelIdOrder("");
                            session.setBengkelId("");
                            showDoneAlert();
                            return null;
                        }

                        @Override
                        public String onError(VolleyError result) {
                            return null;
                        }
                    });
                }
            }
        });

        setUp();
        return v;
    }
    public void setUp(){
        try {
            JSONObject data = new JSONObject(content);
            txtNamaBengkel.setText(data.getString("nama"));
            txtStatus.setText("Anda sudah melakukan konfirmasi pembayaran sebesar Rp"+logic.thousand(""+data.getInt("bill_amount"))+"\nHarap tunggu proses verifikasi pembayaran Anda");
            if(data.getString("jobcode").equals("66")){

                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), CartActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void showDoneAlert(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_bengkel_selesai);
        dialog.setCancelable(true);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
