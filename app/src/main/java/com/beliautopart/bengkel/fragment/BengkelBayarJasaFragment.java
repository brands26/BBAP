package com.beliautopart.bengkel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.activity.SearchActivity;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.BankModel;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelBayarJasaFragment extends Fragment {
    private View v;
    private Context context;
    private SessionManager session;
    private String content;
    private LinearLayout vewRekening;
    private OrderService order;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private RelativeLayout btnBayar;
    private BengkelService bengkelService;
    private TextView txtBiaya;
    private RelativeLayout btnChat;
    private Logic logic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_bengkel_bayar_jasa, container, false);
        context = getContext();
        order = new OrderService(context);
        session = new SessionManager(context);
        bengkelService = new BengkelService(context);
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
        vewRekening = (LinearLayout) v.findViewById(R.id.linierLayoutRekening);
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        txtBiaya  = (TextView) v.findViewById(R.id.txtBiaya);
        btnBayar  = (RelativeLayout) v.findViewById(R.id.lbtnBayar);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogKonfirmasi();
            }
        });
        btnChat  = (RelativeLayout) v.findViewById(R.id.lbtnChat);

        setUp(content);

        return v;
    }
    public void setUp(String content){
        try {
            final JSONObject data = new JSONObject(content);
            session.setBengkelUserId(data.getString("uid"));
            txtNamaBengkel.setText(data.getString("nama"));
            txtBiaya.setText("Bengkel sudah memposting biaya jasa yang Anda sepakati sebesar Rp "+logic.thousand(""+data.getInt("bill_amount")));
            Log.d("bank ",data.getString("bank_list"));
            setrekening(data.getString("bank_list"));
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(session.getBengkelIdOrder().equals("")){
                            Intent i = new Intent(context, SearchActivity.class);
                            i.putExtra("kat", data.getString("jnsbengkel"));
                            startActivity(i);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                        else{
                            Intent i = new Intent(context, CartActivity.class);
                            i.putExtra("kat", data.getString("jnsbengkel"));
                            startActivity(i);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setrekening(String listRekening){
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(listRekening);
            Log.d("arrData ",arrData.toString());
            for(int i = 0; i < arrData.length(); i++)
            {
                JSONObject data = arrData.getJSONObject(i);
                addVewRekening( data.getInt("id"),data.getString("nama_bank")+" "+data.getString("kantor_cabang"),data.getString("norek"), data.getString("nama_rek"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addVewRekening(int no,String nama, String noRek, String pt){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_rekening, null);
        TextView txtNomer = (TextView) child.findViewById(R.id.txtNomer);
        TextView txtNamaBank = (TextView) child.findViewById(R.id.txtNamaBank);
        TextView txtNoRekening = (TextView) child.findViewById(R.id.txtNoRekening);
        TextView txtPt = (TextView) child.findViewById(R.id.txtPt);
        txtNomer.setText(""+no);
        txtNamaBank.setText(nama);
        txtNoRekening.setText("No. Rekening: " +noRek);
        txtPt.setText("a/n. "+pt);
        vewRekening.addView(child);
    }
    private void onDialogKonfirmasi() {
        final String[] norek = {""};
        final int[] kodeBank = {0};
        final List<BankModel> listBank = new ArrayList<BankModel>();
        final List<BankModel> bankPerusahaan = new ArrayList<BankModel>();
        order.getBank(new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject content = new JSONObject(obj.getString("content"));

                        JSONArray arrayBankPerusahaan = new JSONArray(content.getString("bank_perusahaan"));
                        JSONArray arrayListPerusahaan = new JSONArray(content.getString("bank_list"));

                        for (int i = 0; i < arrayBankPerusahaan.length(); i++) {
                            JSONObject itemData = arrayBankPerusahaan.getJSONObject(i);
                            BankModel bankModel = new BankModel(itemData.getInt("id_bank"), itemData.getString("nama_bank"));
                            bankPerusahaan.add(bankModel);

                        }
                        for (int i = 0; i < arrayListPerusahaan.length(); i++) {
                            JSONObject listData = arrayListPerusahaan.getJSONObject(i);
                            listBank.add(new BankModel(listData.getInt("id"), listData.getString("nama")));
                        }

                        JSONObject profileArray = new JSONObject(content.getString("profile"));
                        JSONObject profile = new JSONObject(profileArray.getString("profile"));

                        Log.d("content", profile.getString("bank"));


                        kodeBank[0] = profile.getInt("bank");
                        norek[0] = profile.getString("norek");



                    }
                } catch (JSONException e) {
                }

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.setContentView(R.layout.dialog_konfirm_pembayaran);
                dialog.setCancelable(true);
                final Spinner spBankPerusahaan = (Spinner) dialog.findViewById(R.id.spBankPerusaan);
                final Spinner spBankList = (Spinner) dialog.findViewById(R.id.spBankList);
                final EditText inputRekening = (EditText) dialog.findViewById(R.id.inputNomorRekening);
                Button btnKonfirmasi = (Button) dialog.findViewById(R.id.btnKonfirmasi);
                Button btnBatal = (Button) dialog.findViewById(R.id.btnSimpan);

                ArrayAdapter<BankModel> dataAdapter = new ArrayAdapter<BankModel>(getContext(),
                        android.R.layout.simple_spinner_item, bankPerusahaan);
                spBankPerusahaan.setAdapter(dataAdapter);
                ArrayAdapter<BankModel> listAdapter = new ArrayAdapter<BankModel>(getContext(),
                        android.R.layout.simple_spinner_item, listBank);
                spBankList.setAdapter(listAdapter);
                for(int a=0;a<listBank.size();a++){
                    BankModel bank = listBank.get(a);
                    if(kodeBank[0]==bank.getId()){
                        spBankList.setSelection(a);
                        inputRekening.setText(norek[0]);
                    }
                }
                btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BengkelActivity)getActivity()).startOnLoadingAnimation();
                        int bankpengirim;
                        int bankdikirim;
                        BankModel bank1 = listBank.get(spBankList.getSelectedItemPosition());
                        bankpengirim = bank1.getId();
                        BankModel bank2 = bankPerusahaan.get(spBankPerusahaan.getSelectedItemPosition());
                        bankdikirim = bank2.getId();
                        bengkelService.setPembayaranBiaya(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(),bankpengirim,bankdikirim,inputRekening.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                dialog.dismiss();
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                    }
                });
                btnBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
}
