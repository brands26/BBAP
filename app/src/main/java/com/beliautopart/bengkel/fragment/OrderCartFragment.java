package com.beliautopart.bengkel.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.AuthenticationActivity;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.activity.SearchActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;
import com.beliautopart.bengkel.model.OrderModel;
import com.beliautopart.bengkel.utils.GPSTracker;
import com.beliautopart.bengkel.webservices.OrderService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon on 03/06/16.
 */
public class OrderCartFragment extends Fragment implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 2;
    private View v;
    private TextView txJumlahItem;
    private LinearLayout layoutCartItem;
    private SessionManager session;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Logic logic;
    private List<ItemProduk> cartList;
    private int jumlahCartList;
    private Activity context;
    private ProgressDialog pDialog;
    private Location currentLocation;
    private double currentLatitude=0;
    private double currentLongitude=0;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private LatLng markerLocation;
    private Marker marker;
    private GPSTracker gps;
    private RelativeLayout layoutLoading;
    private OrderService order;
    private String alamat = "null";
    private RelativeLayout lbtnAdd;
    private RelativeLayout lbtnClear;
    private RelativeLayout lbtnPeta;
    private RelativeLayout lbtnProses;
    private Handler handler;
    private LocationManager locationManager;
    private Dialog dialogError;
    private TextView dialogErrorMessage;
    private Button dialogErrorYes;
    private RelativeLayout card_view_progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_order_cart, container, false);
        context = (Activity) getContext();
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);
        dialogErrorMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        dialogErrorYes = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        dialogErrorMessage.setText("Beberapa item yang anda pesan tidak tersedia.\nAnda akan mendapat notifikasi jika item sudah tersedia dan kami akan memproses barang yang tersedia terlebih dahulu.\nAnda ingin melanjutkannya?");
        dialogErrorYes.setText("Lanjutkan");

        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        order = new OrderService(context);
        handler = new Handler();
        logic = new Logic();
        txJumlahItem = (TextView) v.findViewById(R.id.txtJumlahItem);
        layoutCartItem = (LinearLayout) v.findViewById(R.id.layoutCartList);
        layoutLoading = (RelativeLayout) getActivity().findViewById(R.id.layoutLoading);
        card_view_progress = (RelativeLayout) getActivity().findViewById(R.id.relativeLayout12);
        lbtnAdd = (RelativeLayout) v.findViewById(R.id.lbtnAdd);
        lbtnClear = (RelativeLayout) v.findViewById(R.id.lbtnClear);
        lbtnPeta = (RelativeLayout) v.findViewById(R.id.lbtnPeta);
        lbtnProses = (RelativeLayout) v.findViewById(R.id.lbtnProses);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, SearchActivity.class);
                i.putExtra("kat", ((CartActivity) getActivity()).getKat());
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        lbtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                layoutCartItem.removeAllViewsInLayout();
                txJumlahItem.setText(session.getCountOrder()+" item");
                */
                ((CartActivity) getActivity()).stopGetStatusOrder();
                AppController.getInstance().cancelPendingRequests("volley");
                session.emptyCart();
                session.setOrderAktif(false);
                Intent i = new Intent(context, SearchActivity.class);
                i.putExtra("kat", ((CartActivity) getActivity()).getKat());
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                getActivity().finish();
            }
        });
        lbtnPeta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ( Build.VERSION.SDK_INT < 23 ||
                        ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mMap.setMyLocationEnabled(true);
                }
                else{
                    if (ActivityCompat.shouldShowRequestPermissionRationale( context,
                            Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);

                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_ACCESS_COARSE_LOCATION);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                if(session.getUserlat()==0 && session.getUserlng()==0){
                    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {

                        @Override
                        public void onMyLocationChange (Location location) {

                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                        }
                    };
                }
                else{
                    currentLatitude = session.getUserlat();
                    currentLongitude = session.getUserlng();
                }
                LatLng loc = new LatLng(currentLatitude, currentLongitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                if(marker!=null)
                    mMap.clear();
                marker = mMap.addMarker(new MarkerOptions().position(loc).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));

                mMap.setOnMyLocationChangeListener(myLocationChangeListener);
                /*
                gps = new GPSTracker(context);
                if (gps.canGetLocation()) {
                    FindLocation();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Peringatan GPS");
                    alertDialog.setMessage("GPS tidak aktif. Aktifkan sekarang?");
                    alertDialog.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                */
            }
        });
        lbtnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!session.isLoggedIn()){
                    Intent i = new Intent(context,AuthenticationActivity.class);
                    i.putExtra("page","login");
                    i.putExtra("order",true);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else if(session.isLoggedIn() && currentLatitude!=0 && currentLongitude!=0) {
                    final List<ItemProduk> itemIndent = new ArrayList<ItemProduk>();
                    final List<ItemProduk> cart = session.getCart();
                    int size = session.getCart().size();
                    final JSONArray cartArray = new JSONArray();
                    for (int a = 0; a < size; a++) {
                        ItemProduk item = cart.get(a);
                        if (item.getSts() == 0)
                            itemIndent.add(item);
                        JSONObject dataCart = new JSONObject();
                        try {
                            dataCart.put("id", item.getIdItem());
                            dataCart.put("jumlah", item.getJumlah());
                            dataCart.put("harga", item.getHarga());
                            dataCart.put("status", item.getSts());
                            cartArray.put(dataCart);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    final OrderModel orderModel = new OrderModel(session.getUserId(), "0", currentLatitude, currentLongitude, alamat, session.getBengkelId());
                    if (itemIndent.size() > 0) {
                        final Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
                        final Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
                        btnBatal.setVisibility(View.GONE);
                        order.setOrder(orderModel, cartArray.toString(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                try {
                                    dialogError.dismiss();
                                    if(itemIndent.size()==cart.size()){

                                        if (dialogError != null && !dialogError.isShowing())
                                            dialogError.show();

                                        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ((CartActivity) getActivity()).startOnLoadingAnimation();
                                                Activity activity = getActivity();
                                                session.setOrderAktif(false);
                                                session.setOrderId("");
                                                session.setOrderStatus("");
                                                if(activity!=null)
                                                    activity.finish();
                                                dialogError.dismiss();

                                            }
                                        });
                                    }
                                    else{
                                        JSONObject obj = new JSONObject(result);
                                        // check for error
                                        if (!obj.getBoolean("error")) {
                                            JSONObject dataOrder = new JSONObject(obj.getString("content"));
                                            if (session.getbengkelAktif()) {
                                                session.setBengkelIdOrder(dataOrder.getString("idorder"));
                                                ((CartActivity) getActivity()).startGetStatusOrder();
                                            } else {
                                                session.setOrderAktif(true);
                                                session.setOrderId(dataOrder.getString("idorder"));
                                                session.setOrderStatus(dataOrder.getString("statusorder"));
                                                ((CartActivity) getActivity()).startGetStatusOrder();
                                            }
                                            session.emptyCart();
                                        } else {
                                            Toast.makeText(context, obj.getString("content"), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                } catch (JSONException e) {
                                }
                                return result;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                    }
                    else {

                        ((CartActivity) getActivity()).startOnLoadingAnimation();
                        order.setOrder(orderModel, cartArray.toString(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    // check for error
                                    if (!obj.getBoolean("error")) {
                                        JSONObject dataOrder = new JSONObject(obj.getString("content"));
                                        if(session.getbengkelAktif()){
                                            session.setBengkelIdOrder(dataOrder.getString("idorder"));
                                            ((CartActivity)getActivity()).startGetStatusOrder();
                                        }
                                        else{
                                            session.setOrderAktif(true);
                                            session.setOrderId(dataOrder.getString("idorder"));
                                            session.setOrderStatus(dataOrder.getString("statusorder"));
                                            ((CartActivity)getActivity()).startGetStatusOrder();
                                        }
                                        session.emptyCart();
                                    } else {
                                        Toast.makeText(context, obj.getString("content"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                }
                                return result;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                    }
                }
                else if(currentLatitude==0 && currentLongitude==0){
                    Toast.makeText(context,"maaf kami tidak mendapatkan lokasi anda saat ini,tekan peta untuk membantu kami mendapatkan lokasi anda",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(context, AuthenticationActivity.class);
                    context.startActivity(i);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        session = new SessionManager(getContext());
        order = new OrderService(context);
        logic = new Logic();
        jumlahCartList = session.getCountOrder();
        cartList = session.getCart();
        txJumlahItem.setText(jumlahCartList+" Item");

        layoutCartItem.removeAllViewsInLayout();
        for(int i = 0; i < jumlahCartList; i++)
        {
            addVewCart(i);
        }
        if(currentLocation!=null){
            setMapLocation(currentLatitude,currentLongitude);

        }

        return v;
    }
    @Override
    public void onResume(){
        super.onResume();
        session = new SessionManager(getContext());
        order = new OrderService(context);
        logic = new Logic();
        jumlahCartList = session.getCountOrder();
        cartList = session.getCart();
        txJumlahItem.setText(jumlahCartList+" item");

        layoutCartItem.removeAllViewsInLayout();
        for(int i = 0; i < jumlahCartList; i++)
        {
            addVewCart(i);
        }
        if(currentLocation!=null){
            setMapLocation(currentLatitude,currentLongitude);

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-6.2295712, 106.7594778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
        lbtnPeta .performClick();
    }
    public void addVewCart(final int i){
        final ItemProduk item = cartList.get(i);
        final View child =  getActivity().getLayoutInflater().inflate(R.layout.row_cart_list, null);
        NetworkImageView imgProduk = (NetworkImageView) child.findViewById(R.id.imgProduk);
        TextView txtNama = (TextView) child.findViewById(R.id.txtNamaProduk);
        TextView txtKodeProduk = (TextView) child.findViewById(R.id.txtKodeProduk);
        final TextView txtJumlah = (TextView) child.findViewById(R.id.txtJumlah);
        final TextView txtHarga = (TextView) child.findViewById(R.id.txtHarga);
        final TextView txtNegara = (TextView) child.findViewById(R.id.txtNegara);
        final TextView txtTotalHarga = (TextView) child.findViewById(R.id.txtTotalHarga);
        Button btPlus = (Button) child.findViewById(R.id.btnPlus);
        Button btnNegative = (Button) child.findViewById(R.id.btnNegative);
        Button btnBatal = (Button) child.findViewById(R.id.btnSimpan);
        String namaBarang = "";
        if(item.getNamaItem().length()>18){
            namaBarang= item.getNamaItem().substring(0,18);
        }
        else
            namaBarang = item.getNamaItem();
        txtNama.setText(namaBarang);
        txtKodeProduk.setText(item.getKode());
        txtJumlah.setText(""+item.getJumlah());
        txtHarga.setText(""+logic.thousand(""+item.getHarga()));
        txtNegara.setText("Made in:"+item.getMade());
        txtTotalHarga.setText(logic.thousand(""+(item.getHarga()*item.getJumlah())));
        imgProduk.setImageUrl("http://beliautopart.com/_produk/thumbnail/" + item.getFoto().replace(" ", "%20"),imageLoader);
        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getSts()==0){
                    if(item.getJumlah()<session.getSettingMaxBeli()){
                        int jumlah = item.getJumlah() + 1;
                        int harga = jumlah*item.getHarga();
                        item.setJumlah(jumlah);
                        txtJumlah.setText(""+jumlah);
                        txtTotalHarga.setText(""+logic.thousand(""+harga));
                        session.addNewCartList(cartList);
                        txJumlahItem.setText(session.getCountOrder()+" item");
                    }
                    else{
                        Toast.makeText(getContext(),"Maaf sudah melewati batas jumlah pembelian item",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(item.getSts()<session.getSettingMaxBeli()){
                        if(item.getJumlah()<item.getSts()){
                            int jumlah = item.getJumlah() + 1;
                            int harga = jumlah*item.getHarga();
                            item.setJumlah(jumlah);
                            txtJumlah.setText(""+jumlah);
                            txtTotalHarga.setText(""+logic.thousand(""+harga));
                            session.addNewCartList(cartList);
                            txJumlahItem.setText(session.getCountOrder()+" item");
                        }
                        else{
                            Toast.makeText(getContext(),"Maaf sudah melewati batas jumlah pembelian item",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        if(item.getJumlah()<session.getSettingMaxBeli()){
                            int jumlah = item.getJumlah() + 1;
                            int harga = jumlah*item.getHarga();
                            item.setJumlah(jumlah);
                            txtJumlah.setText(""+jumlah);
                            txtTotalHarga.setText(""+logic.thousand(""+harga));
                            session.addNewCartList(cartList);
                            txJumlahItem.setText(session.getCountOrder()+" item");
                        }
                        else{
                            Toast.makeText(getContext(),"Maaf sudah melewati batas jumlah pembelian item",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getJumlah()>1){
                    int jumlah = item.getJumlah() - 1;
                    int harga = jumlah*item.getHarga();
                    item.setJumlah(jumlah);
                    txtJumlah.setText(""+jumlah);
                    txtTotalHarga.setText(""+logic.thousand(""+harga));
                    session.addNewCartList(cartList);
                    txJumlahItem.setText(session.getCountOrder()+" item");
                }
                else{
                    cartList.remove(i);
                    session.addNewCartList(cartList);
                    layoutCartItem.removeView(child);
                    jumlahCartList = session.getCountOrder();
                    cartList = session.getCart();
                    txJumlahItem.setText(session.getCountOrder()+" item");
                    layoutCartItem.removeAllViewsInLayout();
                    for(int i = 0; i < jumlahCartList; i++)
                    {
                        addVewCart(i);
                    }
                    if(session.getCart().size()==0){
                        session.emptyCart();
                        session.setOrderAktif(false);
                        Intent i = new Intent(context, SearchActivity.class);
                        i.putExtra("kat", ((CartActivity)getActivity()).getKat());
                        startActivity(i);
                        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        context.finish();
                    }
                }
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutCartItem.addView(child);
    }
    public void FindLocation() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentLocation==null)
                    showDialog();
            }
        }, 180000);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Mendapatkan koordinat saat ini");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));

        pDialog.show();

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }


    void updateLocation(Location location) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if(currentLocation==null){
            currentLocation = location;
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
            Log.d("location saat ini",""+currentLocation);
            setMapLocation(currentLatitude,currentLongitude);

        }

    }
    public void setMapLocation(double lat,double lng){
        markerLocation = new LatLng(lat, lng);
        if(marker!=null)
            mMap.clear();
        marker = mMap.addMarker(new MarkerOptions().position(markerLocation).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Dragging");
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                markerLocation = marker.getPosition();
            }

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Started");

            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLocation,15));
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String cityName = null;
        String stateName = null;
        String countryName = null;
        try {
            addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);
            alamat = cityName + ", "+stateName+ ", " + countryName;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_alert_order_aktif);
        TextView txtDetail = (TextView) dialog.findViewById(R.id.textView27);
        txtDetail.setText("mohon maaf kami mengalami kesulitan mendeteksi lokasi Anda. Silakan coba lagi");
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
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            if(marker!=null)
                mMap.clear();
            marker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };

    /**
     * Get provider name.
     * @return Name of best suiting provider.
     * */
    String getProviderName() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }
}
