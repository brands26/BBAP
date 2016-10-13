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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.ProgressBarAnimation;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Brandon Pratama on 24/06/2016.
 */
public class OrderShipmentRejectFragment extends Fragment {
    private View v;
    private Context context;
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private TextView txtnamaKurir;
    private TextView txtHpKurir;
    private NetworkImageView imgKurir;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private OrderService order;
    private SessionManager session;
    private TextView txtNominal;
    private TextView txtMetodeBayar;
    private TextView txtWaktu;
    private RelativeLayout layoutLoading;
    private String idOrder;
    private Logic logic;
    private RelativeLayout btnSelesai;
    private TextView txtItem;
    private LinearLayout layoutMain;
    private ArrayList<ItemProduk> itemlist;
    private Dialog dialogStatus;
    private Button btnOK;
    private TextView progressText;
    private ProgressBar progressBar2;
    private ProgressBarAnimation anim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_shipment_reject, container, false);
        context = getContext();
        order = new OrderService(context);
        session = new SessionManager(context);
        logic = new Logic();
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Done");
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNoOrder.setTypeface(tf);
        layoutMain = (LinearLayout) v.findViewById(R.id.layoutMain);
        layoutLoading = (RelativeLayout) getActivity().findViewById(R.id.layoutLoading);


        btnSelesai = (RelativeLayout) v.findViewById(R.id.relativeLayout42);

        content = getArguments().getString("content");
        txtnamaKurir = (TextView) v.findViewById(R.id.txtNamaKurir);
        txtHpKurir = (TextView) v.findViewById(R.id.txtHpKurir);
        txtNominal = (TextView) v.findViewById(R.id.txtNominal);
        txtMetodeBayar = (TextView) v.findViewById(R.id.txtMetodeBayar);
        txtWaktu = (TextView) v.findViewById(R.id.txtWaktu);
        imgKurir = (NetworkImageView) v.findViewById(R.id.imgKurir);
        if(session.getbengkelAktif()){
            idOrder =session.getBengkelIdOrder();
        }
        else{
            idOrder =session.getOrderId();
        }

        setUp(content);
        dialogStatus = new Dialog(getContext());
        dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStatus.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogStatus.setContentView(R.layout.dialog_alert_order_aktif);
        TextView dialog = (TextView) dialogStatus.findViewById(R.id.textView27);
        dialog.setText("Terima kasih telah menggunakan jasa Beliautopart.");
        dialogStatus.setCancelable(false);
        btnOK = (Button) dialogStatus.findViewById(R.id.btnOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setOrderAktif(false);
                session.setOrderId("");
                session.setOrderStatus("");
                session.setBengkelIdOrder("");
                getActivity().finish();
            }
        });


        btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
        return v;
    }

    private void showdialog() {

        if(!dialogStatus.isShowing())
            dialogStatus.show();
        else{
            session.setOrderAktif(false);
            session.setOrderId("");
            session.setOrderStatus("");
            session.setBengkelIdOrder("");
            getActivity().finish();
        }
    }

    public void setUp(String content){
        try {
            ((CartActivity)getActivity()).stopGetStatusOrder();
            JSONArray data = new JSONArray(content);
            int jumlah = data.length();
            for(int a=0;a<jumlah;a++){
                JSONObject d = data.getJSONObject(a);
                txtNoOrder.setText("Order No. "+d.get("nomor"));
                Log.d("",""+content);
                String foto="";
                if(d.getString("foto").equalsIgnoreCase("http://simkurir.beliautopart.com/fotokurir/thumbnail/") || d.getString("foto").equalsIgnoreCase("null"))
                    foto = "http://simkurir.beliautopart.com/fotokurir/unknown.jpg";
                else{
                    foto = d.getString("foto");
                }
                addVewKurir(d.getString("nama"),d.getString("id_item"),d.getString("kode_item"),d.getString("kode_universal"), d.getString("item"),d.getString("qty"),d.getString("cek"), d.getString("hp"),foto, d.getString("id_kurir"),d.getString("kurir_kode"));
            }
            order.getOrderDetail(idOrder,0, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONObject data = new JSONObject(obj.getString("content"));
                        txtMetodeBayar.setText(data.getString("payment_ptype"));
                        Date time = new Date(Long.parseLong(data.getString("create_date"))* (long) 1000);
                        String vv = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(time);
                        txtWaktu.setText(vv);
                        txtNominal.setText("Rp"+logic.thousand(""+data.getInt("total_harga")));
                        return null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ((CartActivity)getActivity()).stopOnLoadingAnimation();
                    return  result;
                }

                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void addVewKurir(String nama, String id_item,String kode_item, String kode_universal, String item, String jumlah,String cek, String no, String foto, final String idKurir, final String kode){
        final View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_shipment_kurir_reject, null);

        itemlist = new ArrayList<>();
        TextView txtnamaKurir = (TextView) child.findViewById(R.id.txtNamaKurir);
        TextView txtHpKurir = (TextView) child.findViewById(R.id.txtHpKurir);
        NetworkImageView imgKurir = (NetworkImageView) child.findViewById(R.id.imgKurir);
        TextView txtListItem = (TextView) child.findViewById(R.id.textView136);
        TextView txtListItemDikembalikan = (TextView) child.findViewById(R.id.textView114);
        LinearLayout listItemLayout = (LinearLayout) child.findViewById(R.id.listItemLayout);
        LinearLayout listItemLayout2 = (LinearLayout) child.findViewById(R.id.listItemLayout2);
        txtnamaKurir.setText(nama);
        txtHpKurir.setText(no);
        imgKurir.setImageUrl(foto,imageLoader);


        String[] items = item.split(",");
        String[] id_items = id_item.split(",");
        String[] kode_items = kode_item.split(",");
        String[] kode_universals = kode_universal.split(",");
        String[] ceks = cek.split(",");
        String[] qty = jumlah.split(",");
        String xitem = "";
        for(int a=0; a<kode_items.length;a++){
            ItemProduk itemProduk = new ItemProduk();
            itemProduk.setIdItem(Integer.parseInt(id_items[a]));
            itemProduk.setKode(kode_items[a]);
            itemProduk.setJumlah(Integer.parseInt(qty[a]));
            itemProduk.setNamaItem(items[a]);
            itemlist.add(itemProduk);
            if(ceks[a].equals("1")){
                txtListItem.setVisibility(View.VISIBLE);
                addVewItemList(itemProduk,listItemLayout );
            }
            else{
                txtListItemDikembalikan.setVisibility(View.VISIBLE);
                addVewItemList(itemProduk,listItemLayout2 );
            }


        }

        layoutMain.addView(child);
    }
    public void addVewItemList( final ItemProduk item, LinearLayout layout ){
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
