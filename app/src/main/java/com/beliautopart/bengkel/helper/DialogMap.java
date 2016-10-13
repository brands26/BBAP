package com.beliautopart.bengkel.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.AuthenticationActivity;
import com.beliautopart.bengkel.activity.SearchActivity;
import com.beliautopart.bengkel.fragment.RegisterFragment;
import com.beliautopart.bengkel.model.RefModel;
import com.beliautopart.bengkel.model.SearchOptionModel;
import com.beliautopart.bengkel.webservices.JobService;
import com.beliautopart.bengkel.webservices.PartsService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brands Pratama on 7/31/2016.
 */
public class DialogMap  extends DialogFragment {
    public String kategori;
    public int Jenis;
    private DIalogSearch.DialogSearchkategori dialogSearchkategori;
    private DIalogSearch.DialogSearchJenis dialogSearchJenis;
    private DIalogSearch.DialogSearchMerk dialogSearchmerk;
    private PartsService partsService;
    private List<RefModel> merk = new ArrayList<>();
    private List<RefModel> tipe = new ArrayList<>();
    private String merkKendaraan=null;
    private String tipeKendaraan=null;
    private String jenis=null;
    private DIalogSearch.DialogSearchTipe dialogSearchtipe;
    private DIalogSearch.DialogSearchParts dialogSearchparts;
    private SessionManager session;
    private RelativeLayout searchLayout;
    private double currentLatitude=0;
    private double currentLongitude=0;
    private JobService jobService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_map, container,
                false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        session = new SessionManager(getContext());
        jobService = new JobService(getContext());
        DialogMapFragment dialogMapFragment = new DialogMapFragment();
        changeFragment(dialogMapFragment);
        // Do something else
        // retrieve display dimensions
        /*
        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        rootView.setMinimumWidth((int)(displayRectangle.width() * 0.8f));
        */
        Button btnSimpan = (Button) rootView.findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                jobService.setLokasi(""+currentLatitude,""+currentLongitude,session.getUserId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(!obj.getBoolean("error")) {
                                getDialog().dismiss();
                                Toast.makeText(getContext(),"Berhasil mengubah lokasi",Toast.LENGTH_SHORT).show();
                            }
                            return null;
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
        });
        return rootView;
    }
    private void changeFragment(Fragment fragment) {
        if(!fragment.isAdded()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogMapFragment extends Fragment implements OnMapReadyCallback {
        private View v;
        private GoogleMap mMap;
        private Activity context;
        private Marker marker;
        private SupportMapFragment mapFragment;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            context = getActivity();
            v = inflater.inflate(R.layout.fragment_dialog_map, container, false);
            mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            return v;
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            LatLng sydney = new LatLng(session.getUserlat(), session.getUserlng());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17));
            marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
            if ( Build.VERSION.SDK_INT < 23 ||
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            }
            else{
                if (ActivityCompat.shouldShowRequestPermissionRationale( context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            mMap.setMyLocationEnabled(true);
            GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange (Location location) {
                    if(currentLatitude==0 && currentLongitude==0){

                        LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                        if(marker!=null)
                            mMap.clear();
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                    }

                }
            };
            mMap.setOnMyLocationChangeListener(myLocationChangeListener);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latlng) {
                    // TODO Auto-generated method stub

                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));


                }
            });
        }

    }
}