package com.beliautopart.bengkel.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.model.ItemProduk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 09/06/2016.
 */
public class PaymentVerivfikasiFragment  extends Fragment {
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
    private LinearLayout layoutWarehouse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_payment_verifikasi, container, false);
        context = getContext();
        logic = new Logic();
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Payment");
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.step3);
        txtNoOrder = (TextView) v.findViewById(R.id.txtNomorOrder);
        layoutWarehouse = (LinearLayout) v.findViewById(R.id.layoutWarehouse);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNoOrder.setTypeface(tf);
        content = getArguments().getString("content");
        setUp(content);
        return v;
    }

    public void setUp(String content) {
        try {
            JSONObject data = new JSONObject(content);
            txtNoOrder.setText("Order No. "+data.get("nomor"));
            JSONArray dataListBarang = data.getJSONArray("listbarangMulti");
            Log.d("total",""+dataListBarang);
            int dataJumlah = dataListBarang.length();
            if(dataJumlah!=0){
                for(int a=0;a<dataJumlah;a++){
                    JSONObject dataItem = dataListBarang.getJSONObject(a);
                    addVewwarehouse(dataItem.getString("nama"),dataItem.getString("kode_item"),dataItem.getString("titles"),dataItem.getString("qtys"),dataItem.getString("resp_distance"),dataItem.getString("resp_estimasi"),dataItem.getString("alamat"));
                }
            }
            else{
                String warehouseItem="";
                String warehouseTotal="";
                String warehousekode="";
                for(int a=0;a<dataJumlah;a++){
                    JSONObject dataItem = dataListBarang.getJSONObject(a);
                    String namaItem = dataItem.getString("nama_item");
                    String kodeItem = dataItem.getString("kode_item");
                    String totalItem = dataItem.getString("qty");
                    warehouseItem=warehouseItem+namaItem+",";
                    warehouseTotal=warehouseTotal+totalItem+",";
                    warehousekode=warehousekode+kodeItem+",";
                }
                addVewwarehouse(data.getString("warehouse_name"),warehousekode,warehouseItem,warehouseTotal,data.getString("warehouse_jarak"),data.getString("resp_estimasi"),data.getString("warehouse_alamat"));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    public void addVewwarehouse(String nama,String kode_item, String item, String jumlah, String jarak,String waktu, String Alamat){
        if(!nama.equals("null")){

            View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_detail_warehouse, null);
            TextView txtNama = (TextView) child.findViewById(R.id.textView52);
            TextView txtStatus = (TextView) child.findViewById(R.id.textView55);
            TextView txtListItem = (TextView) child.findViewById(R.id.textView96);
            LinearLayout litem = (LinearLayout) child.findViewById(R.id.litem);
            txtNama.setText(nama);
            if(waktu.equals("null"))
                waktu="0";
            if(Alamat.equalsIgnoreCase(" ")){
                txtStatus.setText("Lokasi: "+jarak+".\nEstimasi Waktu Pengiriman: "+(Integer.parseInt(waktu)/60)+" menit.");
            }
            else{
                txtStatus.setText(Alamat+".\nLokasi: "+jarak+".\nEstimasi Waktu Pengiriman: "+(Integer.parseInt(waktu)/60)+" menit.");
            }
            String[] items = item.split(",");
            String[] kode_items = kode_item.split(",");
            String[] qty = jumlah.split(",");
            String xitem = "<p>";
            for(int a=0; a<kode_items.length;a++){
                ItemProduk itemProduk = new ItemProduk();
                itemProduk.setJumlah(Integer.parseInt(qty[a]));
                itemProduk.setKode(kode_items[a]);
                itemProduk.setNamaItem(items[a]);
                txtListItem.setVisibility(View.VISIBLE);
                addVewItemList(itemProduk,litem );
            }
            xitem= xitem+"</p>";
            txtListItem.setText(Html.fromHtml(xitem));

            layoutWarehouse.addView(child);

        }
        else{
            View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_detail_warehouse_tidak_ada, null);
            TextView txtListItem = (TextView) child.findViewById(R.id.textView96);
            LinearLayout litem = (LinearLayout) child.findViewById(R.id.litem);
            String[] items = item.split(",");
            String[] kode_items = kode_item.split(",");
            String[] qty = jumlah.split(",");
            for(int a=0; a<items.length;a++){
                ItemProduk itemProduk = new ItemProduk();
                itemProduk.setJumlah(Integer.parseInt(qty[a]));
                itemProduk.setKode(kode_items[a]);
                itemProduk.setNamaItem(items[a]);
                txtListItem.setVisibility(View.VISIBLE);
                addVewItemList(itemProduk,litem );
            }
            layoutWarehouse.addView(child);
        }
    }
    public void addVewItemList(final ItemProduk item, LinearLayout layout ){
        final View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_shipment_item_list, null);
        TextView txtNama = (TextView) child.findViewById(R.id.textView28);
        TextView txtKode = (TextView) child.findViewById(R.id.textView116);
        final LinearLayout listLayout = (LinearLayout) child.findViewById(R.id.listLayout);
        txtNama.setText(item.getNamaItem()+" ("+item.getJumlah()+")");
        txtKode.setText(""+item.getKode());
        txtNama.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        txtKode.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        listLayout.setBackgroundResource(0);
        layout.addView(child);
    }
}
