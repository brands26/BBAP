package com.beliautopart.bengkel.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.RefModel;
import com.beliautopart.bengkel.model.UserModel;
import com.beliautopart.bengkel.utils.GPSTracker;
import com.beliautopart.bengkel.webservices.UserService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputNamaBelakang;
    private EditText inputHp;
    private EditText inputNamaDepan;
    private EditText inputKonfirmPassword;
    private SessionManager session;
    private UserService userService;
    private EditText inputAlamat;
    private ImageButton cartButton;
    private TextView countTextview;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout loadingView;
    private Spinner spProvinsi;
    private Spinner spKab;
    private EditText inputNoRekening;
    private List<RefModel> provinsi = new ArrayList<>();
    private List<RefModel> bank  = new ArrayList<>();
    private List<RefModel> kab  = new ArrayList<>();
    private Spinner spBank;
    private String provinsiId="";
    private String kabID="";
    private String bankId="";
    private RelativeLayout btnSimpan;
    private RelativeLayout btnlogout;
    private RelativeLayout layoutLoading;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private Animation rotation;
    private Animation rotationA;
    private Animation rotationC;
    private ImageButton btnLogout;
    private Dialog dialogError;
    private ArrayAdapter spinnerArrayAdapter3;
    private Button btnLokasiSaatIni;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker marker;
    private GPSTracker gps;
    private Activity context;
    private LatLng lokasiSekarang;
    private ProgressDialog pDialog;
    private Location currentLocation;
    private double currentLatitude;
    private double currentLongitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        context = (Activity)this;
        userService = new UserService(this);
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.activity_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.imgLogo);
        countTextview = (TextView) findViewById(R.id.badge_textView);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        btnSimpan = (RelativeLayout) findViewById(R.id.lbtnSimpan);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);

        btnLogout.setVisibility(View.VISIBLE);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
            }
        });
        txtTitle.setText("Profile");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        session = new SessionManager(this);
        btnback.setVisibility(View.VISIBLE);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputNamaDepan = (EditText) findViewById(R.id.inputNamadepan);
        inputHp = (EditText) findViewById(R.id.inputHp);
        inputAlamat = (EditText) findViewById(R.id.inputAlamat);
        spProvinsi = (Spinner) findViewById(R.id.spPropinsi);
        spKab = (Spinner) findViewById(R.id.spKab);
        inputNoRekening = (EditText) findViewById(R.id.inputNoRekening);
        spBank = (Spinner) findViewById(R.id.spBank);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputKonfirmPassword = (EditText) findViewById(R.id.inputkonfirmPassword);

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnLokasiSaatIni = (Button) findViewById(R.id.btnLokasiSaatIni);
        btnLokasiSaatIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        startOnLoadingAnimation();

        userService.profileUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if(!object.getBoolean("error")){
                        JSONObject object1 = new JSONObject(object.getString("content"));
                        final JSONObject object2 = new JSONObject(object1.getString("profile"));
                        lokasiSekarang = new LatLng(object2.getDouble("gps_lat"),object2.getDouble("gps_long"));
                        marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasiSekarang,15));
                        if(!object2.getString("gps_lat").equals("null") && !object2.getString("gps_lat").equals("null")){
                            currentLatitude = object2.getDouble("gps_lat");
                            currentLongitude = object2.getDouble("gps_long");
                        }
                        JSONArray arrProvinsi = new JSONArray(object1.getString("provinsi"));
                        JSONArray arrBank = new JSONArray(object1.getString("bank"));
                        RefModel refModel =  new RefModel();
                        provinsi.clear();
                        bank.clear();
                        refModel.setNama("pilih");
                        provinsi.add(refModel);
                        bank.add(refModel);
                        kab.add(refModel);
                        for(int a=0;a<arrProvinsi.length();a++){
                            JSONObject data = arrProvinsi.getJSONObject(a);
                            refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                            provinsi.add(refModel);
                        }
                        for(int a=0;a<arrBank.length();a++){
                            JSONObject data = arrBank.getJSONObject(a);
                            refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                            bank.add(refModel);
                        }
                            spProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    Log.d("pos",""+position);
                                    if(position!=0){
                                    RefModel model = provinsi.get(position);
                                    provinsiId = model.getId();
                                    userService.getKab(provinsiId, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            try {
                                                JSONObject resultData = new JSONObject(result);
                                                boolean error = resultData.getBoolean("error");
                                                kab.clear();
                                                RefModel refModel = new RefModel();
                                                refModel.setNama("Pilih");
                                                kab.add(refModel);
                                                if (!error) {
                                                    JSONArray dataArray = resultData.getJSONArray("content");
                                                    int sizeDataArray = dataArray.length();
                                                    for(int a=0;a<sizeDataArray;a++){
                                                        JSONObject data = dataArray.getJSONObject(a);
                                                        refModel = new RefModel(data.getString("id"), data.getString("nama"));
                                                        kab.add(refModel);
                                                    }

                                                    spinnerArrayAdapter3.notifyDataSetChanged();

                                                    int index = 0;
                                                    for(int a=0;a<kab.size();a++){
                                                        RefModel model = kab.get(a);
                                                        if(object2.getString("kabupaten").equals(model.getId()))
                                                            index=a;
                                                    }
                                                    spKab.setSelection(index);

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }

                            });

                        spKab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0){
                                    RefModel model = kab.get(position);
                                    kabID = model.getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0){
                                    RefModel model = bank.get(position);
                                    bankId = model.getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spinnerArrayAdapter3 = new ArrayAdapter<RefModel>(getApplicationContext(),
                                R.layout.spinner_item, kab);
                        spinnerArrayAdapter3.setDropDownViewResource( R.layout.spinner_item );
                        spKab.setAdapter(spinnerArrayAdapter3);
                        ArrayAdapter<RefModel> spinnerArrayAdapter = new ArrayAdapter<RefModel>(getApplicationContext(),
                                R.layout.spinner_item, provinsi);
                        spinnerArrayAdapter.setDropDownViewResource( R.layout.spinner_item );
                        spProvinsi.setAdapter(spinnerArrayAdapter);
                        ArrayAdapter<RefModel> spinnerArrayAdapter2 = new ArrayAdapter<RefModel>(getApplicationContext(),
                                R.layout.spinner_item, bank);
                        spinnerArrayAdapter2.setDropDownViewResource( R.layout.spinner_item );
                        spBank.setAdapter(spinnerArrayAdapter2);
                        int index= 0;
                        if(!object2.getString("propinsi").equals("null")){
                            for(int a=0;a<provinsi.size();a++){
                                RefModel model = provinsi.get(a);
                                if(object2.getString("propinsi").equals(model.getId()))
                                    index=a;
                            }
                            spProvinsi.setSelection(index);
                        }

                        if(!object2.getString("bank").equals("null")) {
                            index= 0;
                            for (int a = 0; a < bank.size(); a++) {
                                RefModel model = bank.get(a);
                                if (object2.getString("bank").equals(model.getId()))
                                    index = a;
                            }
                            spBank.setSelection(index);
                        }
                        inputEmail.setText(object2.getString("email"));
                        inputNamaDepan.setText(object2.getString("nama"));
                        if(!object2.getString("hp").equals("null"))
                            inputHp.setText(object2.getString("hp"));
                        if(!object2.getString("alamat").equals("null"))
                            inputAlamat.setText(object2.getString("alamat"));
                        if(!object2.getString("norek").equals("null"))
                            inputNoRekening.setText(object2.getString("norek"));
                        inputPassword.setText(object2.getString("password"));
                        inputKonfirmPassword.setText(object2.getString("password"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stopOnLoadingAnimation();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });

    }
    public void startOnLoadingAnimation(){
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopOnLoadingAnimation(){
        layoutLoading.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }
    public void onUbahClick(View v){
        if(inputPassword.getText().toString().trim().equals(inputKonfirmPassword.getText().toString().trim())){

            UserModel userModel = new UserModel();
            userModel.setNamaDepan(inputNamaDepan.getText().toString().trim());
            userModel.setHp(inputHp.getText().toString().trim());
            userModel.setEmail(inputEmail.getText().toString().trim());
            userModel.setId(session.getUserId());
            userModel.setAlamat(inputAlamat.getText().toString().trim());
            userModel.setPassword(inputPassword.getText().toString().trim());
            userModel.setKab(kabID);
            userModel.setProvinsi(provinsiId);
            userModel.setBank(bankId);
            userModel.setNorek(inputNoRekening.getText().toString().trim());
            userModel.setLat(currentLatitude);
            userModel.setLng(currentLongitude);

            userService.UpdateUser(userModel, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);

                        // check for error
                        if (!obj.getBoolean("error")) {
                            JSONObject dataUser = new JSONObject(obj.getString("content"));
                            session.setLogin(true);
                            session.setUserId(dataUser.getString("id"));
                            session.setUserNama(dataUser.getString("nama"));
                            session.setUserNamaDepan(dataUser.getString("nama"));
                            session.setUserEmail(dataUser.getString("email"));
                            session.setUserHandphone(dataUser.getString("hp"));
                            session.setUserAlamat(dataUser.getString("alamat"));
                            session.setUserPassword(dataUser.getString("password"));
                            Toast.makeText(context,"Berhasil Mengubah Profil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("content"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                    }
                    return null;
                }

                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });
        }
        else
            Toast.makeText(getApplicationContext(),"Password tidak sama", Toast.LENGTH_SHORT).show();

    }
    public void onKeluarClick(View v){
        session.setLogin(false);
        session.setFID("");
        session.setToken("");
        session.setUserId("");
        session.setUserNama("");
        session.setUserNamaDepan("");
        session.setUserNamaBelakang("");
        session.setUserEmail("");
        session.setUserHandphone("");
        session.setUserAlamat("");
        session.setUserPassword("");
        session.setBengkelAktif(false);
        session.setBengkelId("");
        session.setBengkelUserId("");
        session.setBengkelIdOrder("");
        session.setOrderAktif(false);
        session.setOrderId("");
        session.setOrderStatus("");
        finish();

    }
    public void showDoneAlert(){
        dialogError = new Dialog(this);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);

        TextView txtMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        txtMessage.setText("Anda yakin ingin keluar?");
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Keluar");
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
        btnBatal.setText("Batal");

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeluarClick(v);
                dialogError.dismiss();
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng sydney = new LatLng(-6.2295712, 106.7594778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        lokasiSekarang = new LatLng(lat, lng);
        marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Dragging");
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                lokasiSekarang = marker.getPosition();
            }

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Started");

            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasiSekarang,15));

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
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
