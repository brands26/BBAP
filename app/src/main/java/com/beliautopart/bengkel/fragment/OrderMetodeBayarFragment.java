package com.beliautopart.bengkel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.BankModel;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brands Pratama on 8/2/2016.
 */
public class OrderMetodeBayarFragment  extends Fragment {
    private View v;
    private Context context;
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private TextView txtTime;
    private LinearLayout vewRekening;
    private OrderService order;
    private SessionManager session;
    private RelativeLayout layoutLoading;
    private RelativeLayout btnKonfirmasi;
    private TextView txtNominalPembayaran;
    private String idOrder;
    private Logic logic;
    private ProgressBar progressBar2;
    private TextView progressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_metode_bayar, container, false);
        ((CartActivity)getActivity()).stopGetStatusOrder();
        context = getContext();
        order = new OrderService(context);
        session = new SessionManager(context);
        logic = new Logic();
        vewRekening = (LinearLayout) v.findViewById(R.id.linierLayoutRekening);
        layoutLoading = (RelativeLayout) getActivity().findViewById(R.id.layoutLoading);
        progressBar2 = (ProgressBar) getActivity().findViewById(R.id.progressBar2);
        progressText = (TextView) getActivity().findViewById(R.id.textView106);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Payment");
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNoOrder.setTypeface(tf);
        txtNominalPembayaran =(TextView) v.findViewById(R.id.txtNominalPembayaran);
        txtTime =(TextView) v.findViewById(R.id.txtTimer);
        btnKonfirmasi = (RelativeLayout) v.findViewById(R.id.lbtnkonfirmasi);
        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogKonfirmasi();
            }
        });
        content = getArguments().getString("content");
        if(session.getbengkelAktif()){
            idOrder =session.getBengkelIdOrder();
        }
        else{
            idOrder =session.getOrderId();
        }
        setUp(content);

        return v;
    }
    public void setUp(String nomor){
        try {
            JSONObject data = new JSONObject(getArguments().getString("content"));
            txtNoOrder.setText("Order No. "+data.get("nomor"));
            txtNominalPembayaran.setText("Rp"+logic.thousand(""+data.getInt("total_harga")));
            setrekening(data.getString("bank_list"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setTime(String nomor){
        txtTime.setText(nomor);
    }
    public void setrekening(String listRekening){
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(listRekening);
            Log.d("arrData ",arrData.toString());
            for(int i = 0; i < arrData.length(); i++)
            {
                JSONObject data = arrData.getJSONObject(i);
                addVewRekening(i+1,data.getString("nama_bank")+" "+data.getString("kantor_cabang"),data.getString("norek"),data.getString("nama_rek"),data.getString("kode") );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addVewRekening(int no,String nama, String noRek, String pt,String kodeBank){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_rekening, null);
        TextView txtNomer = (TextView) child.findViewById(R.id.txtNomer);
        TextView txtNamaBank = (TextView) child.findViewById(R.id.txtNamaBank);
        TextView txtNoRekening = (TextView) child.findViewById(R.id.txtNoRekening);
        TextView txtPt = (TextView) child.findViewById(R.id.txtPt);
        TextView txtKOdeTransfer = (TextView) child.findViewById(R.id.txtKOdeTransfer);
        txtNomer.setText(""+no);
        txtNamaBank.setText(nama);
        txtNoRekening.setText("No. Rekening: " +noRek);
        txtPt.setText("a/n. "+pt);
        txtKOdeTransfer.setText("Kode Transfer Antarbank: "+kodeBank);
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
                    }
                }

                inputRekening.setText(norek[0]);

                btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!inputRekening.getText().toString().trim().equals("")){

                            dialog.dismiss();
                            ((CartActivity)getActivity()).startOnLoadingAnimation();
                            int bankpengirim;
                            int bankdikirim;
                            BankModel bank1 = listBank.get(spBankList.getSelectedItemPosition());
                            bankpengirim = bank1.getId();
                            BankModel bank2 = bankPerusahaan.get(spBankPerusahaan.getSelectedItemPosition());
                            bankdikirim = bank2.getId();
                            layoutLoading.setVisibility(View.VISIBLE);
                            order.setKonfirmasiPembayaran(idOrder, session.getUserId(), bankpengirim, bankdikirim, inputRekening.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                                @Override
                                public String onSuccess(String result) {
                                    ((CartActivity)getActivity()).startGetStatusOrder();
                                    return null;
                                }

                                @Override
                                public String onError(VolleyError result) {
                                    return null;
                                }
                            });
                        }
                        else
                            Toast.makeText(context,"Nomor Rekening tidak boleh kosong",Toast.LENGTH_SHORT).show();
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
