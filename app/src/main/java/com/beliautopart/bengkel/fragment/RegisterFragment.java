package com.beliautopart.bengkel.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.AuthenticationActivity;
import com.beliautopart.bengkel.helper.DialogMap;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.UserModel;
import com.beliautopart.bengkel.utils.GPSTracker;
import com.beliautopart.bengkel.webservices.UserService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon on 12/05/16.
 */
public class RegisterFragment extends Fragment implements OnMapReadyCallback {
    private EditText inputEmail;
    private EditText inputPassword;
    private UserService userService;
    private RelativeLayout btnRegister;
    private EditText inputNamaDepan;
    private EditText inputNamaBelakang;
    private EditText inputHp;
    private EditText inputRePassword;
    private SessionManager session;
    private double lat= 0;
    private double lng= 0;
    private Activity context;
    private Button btnLokasi;
    private DialogMap dialogMap;
    private Button btnLokasiSaatIni;
    private GPSTracker gps;
    private GoogleMap mMap;
    private Marker marker;
    private LatLng markerLocation;
    private SupportMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.register_fragment, container, false);

        session = new SessionManager(getContext());
        context = (Activity)getContext();
        if(session.isLoggedIn()){
            Toast.makeText(getContext(),"Anda Sudah Login",Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        dialogMap = new DialogMap();
        inputEmail = (EditText) v.findViewById(R.id.inputEmail);
        inputPassword = (EditText) v.findViewById(R.id.inputPassword);
        inputNamaDepan = (EditText) v.findViewById(R.id.inputNamaDepan);
        inputHp = (EditText) v.findViewById(R.id.inputHp);
        inputRePassword = (EditText) v.findViewById(R.id.inputRePassword);
        btnRegister = (RelativeLayout) v.findViewById(R.id.lbtnregister);
        btnLokasi = (Button) v.findViewById(R.id.btnLokasi);
        userService = new UserService(getContext());

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(inputPassword.getText().toString(),inputRePassword.getText().toString());
                if (inputEmail.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Email belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputPassword.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Password belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputNamaDepan.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Nama Depan belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputHp.getText().toString() == "")
                    Toast.makeText(getContext(), "No Hp belum diisi", Toast.LENGTH_SHORT).show();
                else if (!inputPassword.getText().toString().equals(inputRePassword.getText().toString()))
                    Toast.makeText(getContext(), "Password Tidak Sama", Toast.LENGTH_SHORT).show();
                else
                    onRegister();
            }
        });
        btnLokasiSaatIni = (Button) v.findViewById(R.id.btnLokasiSaatIni);
        btnLokasiSaatIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(context);
                if (gps.canGetLocation()) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    mMap.setMyLocationEnabled(true);
                    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange (Location location) {
                            LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                            if(marker!=null)
                                mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions().position(loc).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }
                    };
                    mMap.setOnMyLocationChangeListener(myLocationChangeListener);
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return v;
    }

    private void onRegister() {
        if(lat!=0 && lng!=0){
            String namaDepan = inputNamaDepan.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String hp = inputHp.getText().toString();
            String password = inputPassword.getText().toString().trim();
            UserModel user = new UserModel("0", namaDepan, "", email, ""+hp, password,lat,lng);
            user.setFID(session.getFID());
            userService.RegisterUser(user, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);

                        // check for error
                        if (!obj.getBoolean("error")) {
                            JSONObject dataUser = new JSONObject(obj.getString("content"));
                            session.setLogin(true);
                            session.setToken(dataUser.getString("uid_firebase"));
                            session.setUserId(dataUser.getString("id"));
                            session.setUserNama(dataUser.getString("nama"));
                            Toast.makeText(getContext(),"Pendaftaran Berhasil", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), obj.getString("content"), Toast.LENGTH_LONG).show();
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
        else{
            Toast.makeText(context,"Anda belum memasukkan lokasi Bengkel, tekan pilih lokasi bengkel untuk memasukkan lokasi bengkel",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-6.2295712, 106.7594778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
        btnLokasiSaatIni .performClick();
    }
}
