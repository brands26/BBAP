package com.beliautopart.bengkel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by brandon on 22/05/16.
 */
public class BayarFragment extends Fragment {
    private View v;
    private TextView txtTimer;
    private RelativeLayout loadingView;
    private CircularProgressView progressView;
    private LinearLayout vewCart;
    private TextView totalCart;
    private TextView txtNoOrder;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LinearLayout vewRekening;
    private TextView txtNoOrderVerifikasi;
    private TextView txtNominal;
    private TextView txtNoRekeningAsal;
    private TextView txtBankAsal;
    private TextView txtBankTujuan;
    private RelativeLayout pembayaranView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_bayar, container, false);
        vewCart = (LinearLayout) v.findViewById(R.id.linierLayoutItemOrder);
        vewRekening = (LinearLayout) v.findViewById(R.id.linierLayoutRekening);
        totalCart = (TextView) v.findViewById(R.id.txtTotalBiaya);
        txtTimer = (TextView) v.findViewById(R.id.txtTimer);
        loadingView = (RelativeLayout) v.findViewById(R.id.loadingLayout);
        pembayaranView = (RelativeLayout) v.findViewById(R.id.pembayaranLayout);
        progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        txtNoOrder = (TextView) v.findViewById(R.id.txtNoOrder);
        txtNoOrderVerifikasi = (TextView) v.findViewById(R.id.txtNoOrderVerifikasi);
        txtNominal = (TextView) v.findViewById(R.id.txtNominal);
        txtBankTujuan = (TextView) v.findViewById(R.id.txtBankTujuan);
        txtBankAsal = (TextView) v.findViewById(R.id.txtBankAsal);
        txtNoRekeningAsal = (TextView) v.findViewById(R.id.txtNoRekeningAsal);
        txtNoOrderVerifikasi = (TextView) v.findViewById(R.id.txtNoOrderVerifikasi);
        return v;
    }
    public void stopAnimation(){
        loadingView.setVisibility(View.GONE);
    }

    public void setTimmer(String time){
        txtTimer.setText(time);
    }
    public void setStatus(String noOrder,String barang,String nominal){
        txtNominal.setText(nominal);
        txtNoOrder.setText("Order No. "+noOrder);
        txtNoOrderVerifikasi.setText("Order No. "+noOrder);
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(barang);
            int Total = 0;
            for(int i = 0; i < arrData.length(); i++)
            {
                JSONObject itemData = arrData.getJSONObject(i);
                Total += itemData.getInt("qty")*itemData.getInt("harga");
                addVewCart(
                        itemData.getString("nama_item"), itemData.getString("kode_item"),
                        itemData.getInt("qty"), itemData.getInt("harga"),
                        "http://beliautopart.com/_produk/thumbnail/" + itemData.getString("namafile").replace(" ", "%20")
                );
            }
            totalCart.setText(""+Total);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setVerifikasi(String bankTujuan,String Nominal,String bankAsal, String rekAsal){
        pembayaranView.setVisibility(View.GONE);
        txtNominal.setText(Nominal);
        txtNoRekeningAsal.setText(rekAsal);
        txtBankAsal.setText(bankAsal);
        txtBankTujuan.setText(bankTujuan);
    }
    public void setrekening(String listRekening){
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(listRekening);
            Log.d("arrData ",arrData.toString());
            int Total = 0;
            for(int i = 0; i < arrData.length(); i++)
            {
                JSONObject data = arrData.getJSONObject(i);
                addVewRekening( data.getInt("id"),data.getString("nama_bank")+" "+data.getString("nama_rek"),data.getString("norek"), data.getString("kantor_cabang"), data.getString("kode"));
            }
            totalCart.setText(""+Total);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addVewCart(String Nama, String kode, int jumlah, int harga, String foto){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_cart, null);
        NetworkImageView imgProduk = (NetworkImageView) child.findViewById(R.id.imgProduk);
        TextView txtNama = (TextView) child.findViewById(R.id.txtNamaProduk);
        TextView txtKodeProduk = (TextView) child.findViewById(R.id.txtKodeProduk);
        TextView txtJumlah = (TextView) child.findViewById(R.id.txtJumlah);
        TextView txtHarga = (TextView) child.findViewById(R.id.txtHarga);
        txtNama.setText(Nama);
        txtKodeProduk.setText(kode);
        txtJumlah.setText("jumlah : "+jumlah);
        txtHarga.setText(""+harga);
        imgProduk.setImageUrl(foto,imageLoader);
        vewCart.addView(child);
    }
    public void addVewRekening(int no,String nama, String noRek, String pt, String kodeTrans){

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
        txtKOdeTransfer.setText("Kode Transfer Antar Bank: "+kodeTrans);
        vewRekening.addView(child);
    }
}
