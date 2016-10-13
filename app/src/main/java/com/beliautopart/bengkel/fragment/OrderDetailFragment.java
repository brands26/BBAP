package com.beliautopart.bengkel.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;
import com.beliautopart.bengkel.webservices.OrderService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by brandon on 05/06/16.
 */
public class OrderDetailFragment extends Fragment {
    private View v;
    private Context context;
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private SupportMapFragment mapFragment;
    private TextView txtStatus;
    private Marker markerAnda;
    private Marker markerwarehouse;
    private GoogleMap mMap;
    private String content;
    private RelativeLayout btnBayar;
    private RelativeLayout layoutLoading;
    private OrderService order;
    private SessionManager session;
    private RelativeLayout btnBatal;
    private Dialog dialogError;
    private String idOrder;
    private TextView txtwarehouse;
    private TextView txtwarehouseStatus;
    private TextView txtItemList;
    private LinearLayout layoutWarehouse;
    private ArrayList<ItemProduk> itemlist;
    private LinearLayout listItemDiterimaLayout;
    private CardView listItemIndenLayout;
    private RelativeLayout card_view_progress;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Logic logic;
    private LinearLayout viewBiaya;
    private LinearLayout viewOrder;
    private int totalBiaya = 0;
    private TextView txtTotalBiaya;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_detail, container, false);
        context = getContext();
        logic = new Logic();
        order = new OrderService(context);
        session = new SessionManager(context);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Order");
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.step2);
        layoutWarehouse = (LinearLayout) v.findViewById(R.id.layoutWarehouse);
        listItemDiterimaLayout = (LinearLayout) v.findViewById(R.id.listItemDiterimaLayout);
        listItemIndenLayout = (CardView) v.findViewById(R.id.card_view_status_reject);
        layoutLoading = (RelativeLayout) getActivity().findViewById(R.id.layoutLoading);

        viewBiaya =(LinearLayout) v.findViewById(R.id.linierBiaya);
        viewOrder =(LinearLayout) v.findViewById(R.id.linierOrder);
        txtTotalBiaya = (TextView) v.findViewById(R.id.textView123);

        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNoOrder.setTypeface(tf);
        txtStatus =(TextView) v.findViewById(R.id.txtStatus);
        //mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        content = getArguments().getString("content");
        btnBayar = (RelativeLayout) v.findViewById(R.id.lbtnBayar);
        if(session.getbengkelAktif()){
            idOrder =session.getBengkelIdOrder();
        }
        else{
            idOrder =session.getOrderId();
        }
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CartActivity)getActivity()).startOnLoadingAnimation();

                order.setPembayaran(idOrder, session.getUserId(), 0, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }
        });
        btnBatal = (RelativeLayout) v.findViewById(R.id.lbtnBatal);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
            }
        });

        setUp(content);
        indenLayoutGone();
        return v;
    }
    public void setUp(String content){
        try {
            JSONObject data = new JSONObject(content);
            txtNoOrder.setText("Order No. "+data.get("nomor"));
            //String status = "Order anda akan dikirim dari\n"+data.getString("warehouse_name")+"\n"+data.getString("warehouse_alamat")+"\n"+data.getString("warehouse_jarak");
            String status = "Order Anda telah kami siapkan, silakan Anda melakukan pembayaran terlebih dahulu.\nPengiriman akan kami lakukan setelah Anda melakukan pembayaran.";
            txtStatus.setText(status);
            JSONArray dataListBarang = data.getJSONArray("listbarangMulti");
            int dataJumlah = dataListBarang.length();
            ArrayList<ItemProduk> listDIterima = new ArrayList<>();
            ArrayList<ItemProduk> listInden = new ArrayList<>();
            for(int a=0;a<dataJumlah;a++) {
                JSONObject dataItem = dataListBarang.getJSONObject(a);
                String[] itemlist = dataItem.getString("titles").split(",");
                String[] itemMade = dataItem.getString("made").split(",");
                String[] itemKode = dataItem.getString("kode_item").split(",");
                String[] itemJumlah = dataItem.getString("qtys").split(",");
                String[] itemharga = dataItem.getString("harga").split(",");
                String[] namafile = dataItem.getString("namafile").split(",");
                for (int b = 0; b < itemKode.length; b++) {
                    addVewOrder(itemlist[b], itemKode[b],itemharga[b],itemMade[b],itemJumlah[b],"http://beliautopart.com/_produk/thumbnail/"+namafile[b]);
                }
                String[] ongkir = dataItem.getString("ongkir").split(",");
                int ongkirSize = ongkir.length;
                for(int b=0;b<ongkirSize;b++){
                    int c= b+1;
                    addVewCart("biaya ongkir " + c, ongkir[b]);
                }
            }
            if(!data.getString("orderno").equals("null"))
                addVewCart("Biaya angka unik", data.getString("orderno"));
            txtTotalBiaya.setText("Rp"+logic.thousand(""+totalBiaya));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void showDoneAlert(){
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);

        TextView txtMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        txtMessage.setText("Anda yakin ingin membatalkan sesi ini?");
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Batalkan");
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
        btnBatal.setText("Tidak");

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CartActivity)getActivity()).stopGetStatusOrder();
                order.setBatal(idOrder, session.getUserId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        Activity activity = getActivity();
                        session.setOrderAktif(false);
                        session.setOrderId("");
                        dialogError.dismiss();
                        Toast.makeText(context,"order telah dibatalkan",Toast.LENGTH_SHORT).show();
                        if(activity!=null)
                            getActivity().finish();
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
                dialogError.dismiss();
            }
        });
        if (dialogError != null && !dialogError.isShowing())
            dialogError.show();
    }
    public void addVewItemList( final ItemProduk item, LinearLayout layout ){
        final View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_shipment_item_list, null);
        TextView txtNama = (TextView) child.findViewById(R.id.textView28);
        TextView txtKode = (TextView) child.findViewById(R.id.textView116);
        TextView txtMade = (TextView) child.findViewById(R.id.textView20);
        TextView txtHarga = (TextView) child.findViewById(R.id.textView117);
        final LinearLayout listLayout = (LinearLayout) child.findViewById(R.id.listLayout);
        txtNama.setText(item.getNamaItem()+" ("+item.getJumlah()+")");
        txtKode.setText(""+item.getKode());
        txtMade.setText("Made In: "+item.getMade());
        txtHarga.setText("Harga: "+item.getHarga());
        txtNama.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        txtKode.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        txtMade.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        txtHarga.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

        listLayout.setBackgroundResource(0);
        layout.addView(child);
    }
    public void addVewOrder(String Nama, String kode,String harga,String made,String jumlah,String url){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_rincian_order_parts, null);
        NetworkImageView img= (NetworkImageView) child.findViewById(R.id.imgProduk);
        TextView txtNama = (TextView) child.findViewById(R.id.txtNamaProduk);
        TextView txtKodeProduk = (TextView) child.findViewById(R.id.txtKodeProduk);
        TextView txtJumlah = (TextView) child.findViewById(R.id.txtJumlah);
        TextView txtHarga = (TextView) child.findViewById(R.id.txtHarga);
        txtNama.setText(Nama);
        txtKodeProduk.setText(kode);
        txtJumlah.setText("Made In:"+made);
        txtHarga.setText(logic.thousand(harga));
        img.setImageUrl(url,imageLoader);
        viewOrder.addView(child);
        addVewCart(Nama+" ("+jumlah+")",""+Integer.parseInt(jumlah)*Integer.parseInt(harga));
    }
    public void addVewCart(String Nama, String harga){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_rincian_biaya, null);
        TextView txtNama = (TextView) child.findViewById(R.id.textView124);
        TextView txtHarga = (TextView) child.findViewById(R.id.textView125);
        txtNama.setText(Nama);
        txtHarga.setText(logic.thousand(harga));
        viewBiaya.addView(child);
        totalBiaya+=Integer.parseInt(harga);
    }

    public void SetStatus(String status){
        txtStatus.setText(status);
    }
    public void indenLayoutGone(){
        listItemIndenLayout.setVisibility(View.GONE);
    }
}
