package com.beliautopart.bengkel.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.activity.TrackingKurirActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;
import com.beliautopart.bengkel.webservices.OrderService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brandon Pratama on 09/06/2016.
 */
public class OrderShipmentFragment extends Fragment {
    private View v;
    private Context context;
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private Logic logic;
    private TextView txtnamaKurir;
    private TextView txtHpKurir;
    private NetworkImageView imgKurir;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private RelativeLayout btnDelivered;
    private String kodeKurir;
    private OrderService order;
    private SessionManager session;
    private RelativeLayout layoutLoading;
    private FragmentTransaction ft;
    private String idOrder;
    private RelativeLayout btnReject;
    private TextView txtItem;
    private LinearLayout layoutMain;
    private ArrayList<ItemProduk> itemlist;
    private Dialog dialogReject;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 200;
    private TextView progressText;
    private ProgressBar progressBar2;
    private int RESULT_OK;
    private ImageView imgReject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_shipment, container, false);
        context = getContext();
        order = new OrderService(context);
        session = new SessionManager(context);
        logic = new Logic();
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Shipment");
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNoOrder.setTypeface(tf);
        layoutMain = (LinearLayout) v.findViewById(R.id.layoutMain);
        layoutLoading = (RelativeLayout) getActivity().findViewById(R.id.layoutLoading);
        content = getArguments().getString("content");
        ft = getActivity().getSupportFragmentManager().beginTransaction();
        setUp(content);

        dialogReject = new Dialog(context);
        dialogReject.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReject.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogReject.setContentView(R.layout.dialog_barang_reject);
        dialogReject.setCancelable(false);
        imgReject = (ImageView) dialogReject.findViewById(R.id.imgReject);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.aksesoris192);
        imgReject.setImageBitmap(icon);


        return v;
    }
    public void setUp(String content){
        Log.d("content",content);
        try {
            JSONArray data = new JSONArray(content);
            int jumlah = data.length();
            for(int a=0;a<jumlah;a++){
                JSONObject d = data.getJSONObject(a);
                txtNoOrder.setText("Order No. "+d.get("nomor"));
                String[] ceks = d.getString("cek").split(",");
                String cek;
                String foto="";
                if(d.getString("foto").equalsIgnoreCase("http://simkurir.beliautopart.com/fotokurir/thumbnail/") || d.getString("foto").equalsIgnoreCase("null"))
                    foto = "http://simkurir.beliautopart.com/fotokurir/unknown.jpg";
                else{
                    foto = d.getString("foto");
                }
                if(ceks.length>1)
                    cek=ceks[a];
                else
                    cek=d.getString("cek");
                if(cek.equals("0"))
                    addVewKurir(d.getString("nama"),d.getString("id_item"),d.getString("kode_item"),d.getString("kode_universal"), d.getString("item"),d.getString("qty"), d.getString("hp"),foto, d.getString("id_kurir"),d.getString("kurir_kode"));
            }
            ((CartActivity)getActivity()).stopLoaing();
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    private void onDialogbarangDiterima(final int status, final String idKurir, final String kode, final  View childView,final ArrayList<ItemProduk> itemlists) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_barang_diterima);
        LinearLayout listitem = (LinearLayout) dialog.findViewById(R.id.itemlist);
        LinearLayout listLayout = (LinearLayout) dialog.findViewById(R.id.linearLayout10);

        for(int a=0; a<itemlists.size();a++){
            ItemProduk itemProduk = itemlists.get(a);
            if(!itemProduk.isReject()){
                listLayout.setVisibility(View.VISIBLE);
                addVewItemList(a,itemProduk,listitem,itemlists,false);
            }

        }
        dialog.setCancelable(true);
        final EditText inputKodeKurir = (EditText) dialog.findViewById(R.id.inputKodeKurir);
        Button btnKonfirmasi = (Button) dialog.findViewById(R.id.btnKonfirmasi);
        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (inputKodeKurir.getText().toString().trim().equals(kode)) {
                    dialog.dismiss();
                    if(status==1){

                        String id ="";
                        if(session.getbengkelAktif())
                            id=session.getBengkelIdOrder();
                        else
                            id=session.getOrderId();

                        String jsonItemLists = new Gson().toJson(itemlists);
                        order.setBarangDiterima(id, session.getUserId(),idKurir,jsonItemLists, new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                childView.setVisibility(View.GONE);

                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                    }

                } else {
                    Toast.makeText(context, "kode kurir salah", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    public void addVewKurir(String nama, String id_item,String kode_item, String kode_universal, String item, String jumlah, String no, String foto, final String idKurir, final String kode){
        final View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_shipment_kurir_detail, null);
        final ArrayList<ItemProduk> itemlistKurir = new ArrayList<>();

        TextView txtnamaKurir = (TextView) child.findViewById(R.id.txtNamaKurir);
        TextView txtHpKurir = (TextView) child.findViewById(R.id.txtHpKurir);
        NetworkImageView imgKurir = (NetworkImageView) child.findViewById(R.id.imgKurir);
        TextView txtListItem = (TextView) child.findViewById(R.id.textView137);
        LinearLayout listItemLayout = (LinearLayout) child.findViewById(R.id.listItemLayout);
        Button btnDelivered = (Button) child.findViewById(R.id.button4);
        Button btnTracking = (Button) child.findViewById(R.id.button3);

        txtnamaKurir.setText(nama);
        txtHpKurir.setText(no);
        imgKurir.setImageUrl(foto,imageLoader);
        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogbarangDiterima(1,idKurir,kode,child,itemlistKurir);
            }
        });
        btnTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, TrackingKurirActivity.class);
                i.putExtra("id",idKurir);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        String[] items = item.split(",");
        String[] id_items = id_item.split(",");
        String[] kode_items = kode_item.split(",");
        String[] kode_universals = kode_universal.split(",");
        String[] qty = jumlah.split(",");
        String xitem = "";
        for(int a=0; a<kode_items.length;a++){
            ItemProduk itemProduk = new ItemProduk();
            itemProduk.setIdItem(Integer.parseInt(id_items[a]));
            itemProduk.setKode(kode_items[a]);
            itemProduk.setJumlah(Integer.parseInt(qty[a]));
            itemProduk.setNamaItem(items[a]);
            itemlistKurir.add(itemProduk);
            addVewItemList(a,itemProduk,listItemLayout,itemlistKurir,true);

        }
        txtListItem.setText(xitem);

        layoutMain.addView(child);
    }

    public void addVewItemList(final int index, final ItemProduk item, LinearLayout layout, final ArrayList<ItemProduk> itemlistKurir, boolean touch){
        final View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_shipment_item_list_margin_bottom_5, null);
        TextView txtNama = (TextView) child.findViewById(R.id.textView28);
        TextView txtKode = (TextView) child.findViewById(R.id.textView116);
        final RelativeLayout listLayout = (RelativeLayout) child.findViewById(R.id.listLayouta);
        final RelativeLayout editLayout = (RelativeLayout) child.findViewById(R.id.editLayout);
        if(item.getNamaItem().length()>20)
            txtNama.setText(item.getNamaItem().substring(0,20)+" ("+item.getJumlah()+")");
        else
            txtNama.setText(item.getNamaItem()+" ("+item.getJumlah()+")");
        txtKode.setText(""+item.getKode());
        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editLayout.getVisibility()==View.VISIBLE)
                    openDialogReject(item);
            }

        });

        if(touch){
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(item.isReject()){
                        editLayout.setVisibility(View.INVISIBLE);
                        item.setReject(false);
                        itemlistKurir.set(index,item);
                        listLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button));
                    }
                    else{
                        editLayout.setVisibility(View.VISIBLE);
                        openDialogReject(item);
                        item.setReject(true);
                        itemlistKurir.set(index,item);
                        listLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_grey));
                    }
                }
            });
            if(item.isReject())
                listLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_grey));
            else
                listLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button));
        }
        else{
            txtNama.setText(item.getNamaItem()+" ("+item.getJumlah()+")");
            txtKode.setText(""+item.getKode());
            txtNama.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
            txtKode.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));

            listLayout.setBackgroundResource(0);
        }
        layout.addView(child);
    }

    private void openDialogReject(final ItemProduk item) {
        if(!dialogReject.isShowing())
            dialogReject.show();
        TextView txtItemList = (TextView) dialogReject.findViewById(R.id.txtItemList);
        Button btnKirim = (Button) dialogReject.findViewById(R.id.btnKirim);
        Button btnBatal = (Button) dialogReject.findViewById(R.id.btnBatal);
        txtItemList.setText(item.getNamaItem()+" ("+item.getJumlah()+")");

        final Spinner spinnerCountShoes = (Spinner)dialogReject.findViewById(R.id.spAlasan);
        final EditText inputReject = (EditText)dialogReject.findViewById(R.id.inputReject);
        final ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, getResources().getStringArray(R.array.alasanReject));
        spinnerCountShoes.setAdapter(spinnerCountShoesArrayAdapter);

        inputReject.setText("");
        if(item.getImageReject()!=null)
            imgReject.setImageBitmap(item.getImageReject());
        else
            imgReject.setImageResource(0);
        spinnerCountShoes.setSelection(0);

        if(!item.getAlasanReject().equals(""))
            inputReject.setText(item.getAlasanReject());
        int id = item.getIdReject()-2;
        if(id<=0)
            spinnerCountShoes.setSelection(0);
        else
            spinnerCountShoes.setSelection(item.getIdReject());


        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputReject.getText().toString().equals("")){
                    Toast.makeText(context,"Deskripsi alasan reject harus diisi.",Toast.LENGTH_SHORT).show();
                }
                else{
                    item.setIdReject(spinnerCountShoes.getSelectedItemPosition()+2);
                    item.setAlasanReject(inputReject.getText().toString().trim());
                    inputReject.setText("");
                    Bitmap bitmap;
                    if(imgReject.getDrawable()!=null){
                        bitmap= ((BitmapDrawable)imgReject.getDrawable()).getBitmap();
                        item.setImageReject(bitmap);
                    }
                    spinnerCountShoes.setSelection(0);
                    dialogReject.dismiss();
                }
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT < 23 ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    selectImage();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                    }
                }
            }
        });

    }


    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else if (options[item].equals("Choose from Gallery"))
                {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    getActivity().startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void setImageReject(Bitmap image) {
        if(dialogReject.isShowing())
            imgReject.setImageBitmap(image);
    }
}