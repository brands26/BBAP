package com.beliautopart.bengkel.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.Logic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 08/06/2016.
 */
public class PaymentFragment extends Fragment {
    private View v;
    private Context context;
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private TextView txtNominal;
    private TextView txtBankTujuan;
    private TextView txtBankAsal;
    private TextView txtNoRekeningAsal;
    private Logic logic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_payment, container, false);
        context = getContext();
        logic = new Logic();
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Payment");
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.step3);
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNoOrder.setTypeface(tf);
        content = getArguments().getString("content");
        txtNominal = (TextView) v.findViewById(R.id.txtNominal);
        txtBankTujuan = (TextView) v.findViewById(R.id.txtBankTujuan);
        txtBankAsal = (TextView) v.findViewById(R.id.txtBankAsal);
        txtNoRekeningAsal = (TextView) v.findViewById(R.id.txtNoRekeningAsal);
        setUp(content);
        return v;
    }

    public void setUp(String content){
        try {
            JSONObject data = new JSONObject(content);
            txtNoOrder.setText("Order No. "+data.get("nomor"));
            txtNominal.setText("Rp"+logic.thousand(""+data.getInt("totalbiaya")));
            txtNoRekeningAsal.setText(data.getString("noRek"));
            txtBankAsal.setText(data.getString("bankPengirim"));
            txtBankTujuan.setText(data.getString("bankTujuan"));
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
