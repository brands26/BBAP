package com.beliautopart.bengkel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.ChatActivity;
import com.beliautopart.bengkel.activity.JobAmbilActivity;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.RefModel;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.beliautopart.bengkel.webservices.JobService;
import com.beliautopart.bengkel.webservices.PartsService;
import com.google.android.gms.location.LocationListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelResponseFragment extends Fragment implements OnMapReadyCallback,LocationListener {
    private View v;
    private Context context;
    private SessionManager session;
    private SupportMapFragment mapFragment;
    private String content;
    private GoogleMap mMap;
    private Marker marker;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private RelativeLayout btnBayar;
    private BengkelService bengkel;
    private RelativeLayout btnBatal;
    private RelativeLayout btnChat;
    private String namaBengkel;
    private Dialog dialogError;
    private TextView tv;
    private RelativeLayout lbtnJasa;
    private List<RefModel> tahun = new ArrayList<>();
    private List<RefModel> merk= new ArrayList<>();
    private List<RefModel> tipe= new ArrayList<>();
    private String tahunKendaraan;
    private String tipeKendaraan;
    private String merkKendaraan;
    private PartsService partsService;
    private JobService jobService;
    private TextView txtStatus;
    private TextView txtInformasi;
    private Marker markerBengkel;
    private LatLng lokasi;
    private String jenisBengkel;
    private boolean salon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        salon = ((JobAmbilActivity)getActivity()).getSalon();

        v = inflater.inflate(R.layout.fragment_bengkel_respon, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);
        partsService = new PartsService(context);
        jobService = new JobService(context);
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel1);
        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);
        content = getArguments().getString("content");
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        txtJarak  = (TextView) v.findViewById(R.id.txtJarak);
        txtStatus  = (TextView) v.findViewById(R.id.textView73);
        txtInformasi  = (TextView) v.findViewById(R.id.textView77);
        btnBatal = (RelativeLayout) v.findViewById(R.id.lbtnSimpan);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
            }
        });
        btnChat = (RelativeLayout) v.findViewById(R.id.lbtnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("nama",namaBengkel);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        lbtnJasa = (RelativeLayout) v.findViewById(R.id.lbtnJasa);
        return v;
    }
    public void setUp(){
        final String merk;
        final String tipe;
        final String tahun;
        final String nopol;
        try {
            Log.d("content",content);
            final JSONObject data = new JSONObject(content);
            tv.setText("Job ID "+session.getBengkelId());
            namaBengkel = data.getString("nama");
            txtNamaBengkel.setText(data.getString("nama"));
            session.setBengkelUserId(data.getString("uid0"));
            txtJarak.setText(data.getString("jarak"));
            merk = data.getString("req_merk");
            tipe = data.getString("req_tipe");
            tahun = data.getString("req_tahun");
            nopol = data.getString("req_nopol");
            jenisBengkel = data.getString("jnsBengkel");
            Log.d("status jenis",jenisBengkel);
            if(mMap!=null){
                LatLng lokasiSekarang = new LatLng(data.getDouble("lat_bengkel"),data.getDouble("lng_bengkel"));
                lokasi = new LatLng(data.getDouble("lat_user"), data.getDouble("lng_user"));
                if(jenisBengkel.equals("1"))
                    markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));
                else
                    markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(lokasiSekarang);

                marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang ).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                builder.include(lokasi);

                final LatLngBounds bounds = builder.build();
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                    }
                });
            }
            if(data.getString("jobcode").equals("40"))
                btnBatal.setVisibility(View.GONE);
            else if(data.getString("jobcode").equals("41") || data.getString("jobcode").equals("42")){
                lbtnJasa.setVisibility(View.GONE);
                txtStatus.setText("Customer telah Meng-Approve Anda untuk Job ini.\nPembayaran dari Customer Telah Diterima oleh Sistem.\nSilakan segera menuju Lokasi Customer Anda.");
                txtInformasi.setText("Mintalah user menekan menekan tombol konfirmasi kedatangan.");
            }

            else if(data.getString("jobcode").equals("4")){
                lbtnJasa.setVisibility(View.VISIBLE);
                btnBatal.setVisibility(View.GONE);
                Date time = new java.util.Date(Long.parseLong(data.getString("arrival_time"))* (long) 1000);
                String vv = new SimpleDateFormat("HH:mm").format(time);
                txtStatus.setText("Customer telah mengkonfirmasikan kedatangan Anda di lokasi pada pukul "+vv);
                txtInformasi.setText("Jika Anda sudah tiba di-lokasi:\n" +
                        "Periksa kerusakan/masalah pada kendaraan Customer.\n" +
                        "Negosiasikan nilai nominal jasa perbaikan/jasa pemasangan spare part yang akan Anda kenakan kepada Customer.\n" +
                        "Klik tombol Jasa untuk input nominal jasa ke sistem agar bisa dibayar oleh Customer.\n");
                btnChat.setVisibility(View.GONE);
                lbtnJasa.setVisibility(View.VISIBLE);
                lbtnJasa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setOnDialogShow(jenisBengkel,merk,tipe,tahun,nopol);

                    }
                });
            }
            else if(data.getString("jobcode").equals("140"))
                btnBatal.setVisibility(View.GONE);
            else if(data.getString("jobcode").equals("141") || data.getString("jobcode").equals("142")){
                lbtnJasa.setVisibility(View.GONE);
                txtStatus.setText("Customer telah Meng-Approve Anda untuk Job ini.\nPembayaran dari Customer Telah Diterima oleh Sistem.\nSilakan segera menuju Lokasi Customer Anda.");
                txtInformasi.setText("Mintalah user menekan menekan tombol konfirmasi kedatangan.");
            }

            else if(data.getString("jobcode").equals("104")){
                lbtnJasa.setVisibility(View.VISIBLE);
                btnBatal.setVisibility(View.GONE);
                Date time = new java.util.Date(Long.parseLong(data.getString("arrival_time"))* (long) 1000);
                String vv = new SimpleDateFormat("HH:mm").format(time);
                txtStatus.setText("Customer telah mengkonfirmasikan kedatangan Anda di lokasi pada pukul "+vv);
                txtInformasi.setText("Jika Anda sudah tiba di-lokasi:\n" +
                        "Kerjakan layanan pada kendaraan Customer.\n" +
                        "Negosiasikan nilai nominal jasa layanan yang akan Anda kenakan kepada Customer.\n" +
                        "Klik tombol Jasa untuk input nominal jasa ke sistem agar bisa dibayar oleh Customer.\n");
                btnChat.setVisibility(View.GONE);
                lbtnJasa.setVisibility(View.VISIBLE);
                lbtnJasa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setOnDialogShow(jenisBengkel,merk,tipe,tahun,nopol);

                    }
                });
            }
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
        btnBatal.setText("tidak");

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bengkel.setPembatalanJob(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        dialogError.dismiss();
                        getActivity().finish();
                        Toast.makeText(context,"Telah dibatalkan",Toast.LENGTH_SHORT).show();
                        session.setBengkelAktif(false);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-6.2295712, 106.7594778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
        setUp();
    }
    public void setOnDialogShow(final String jenis,final String merkInput,final String tipeInput,final String tahunInput,final String nopolInput){
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_input_jasa);
        dialogError.setCancelable(false);
        Button btnKirim = (Button) dialogError.findViewById(R.id.btnKirim);
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);

        TextView txtMerk = (TextView) dialogError.findViewById(R.id.txtMerk);
        TextView txtTipe = (TextView) dialogError.findViewById(R.id.txtTipe);


        if(jenis.equals("1")){
            txtMerk.setText("Merk Motor");
            txtTipe.setText("Tipe Motor");
        }
        else{
            txtMerk.setText("Merk Mobil");
            txtTipe.setText("Tipe Mobil");
        }



        final EditText inputNomorPolisi = (EditText) dialogError.findViewById(R.id.inputNomorPolisi);
        inputNomorPolisi.setText(nopolInput);
        final EditText inputKerusakan = (EditText) dialogError.findViewById(R.id.inputNominal);
        final Spinner spTahun = (Spinner) dialogError.findViewById(R.id.spTahun);
        final Spinner spMerk = (Spinner) dialogError.findViewById(R.id.spMerk);
        final Spinner spTipe = (Spinner) dialogError.findViewById(R.id.spTipe);

        final ArrayAdapter<RefModel> spinnerArrayAdapter2 = new ArrayAdapter<RefModel>(context,
                R.layout.spinner_item, merk);
        spinnerArrayAdapter2.setDropDownViewResource( R.layout.spinner_item );
        spMerk.setAdapter(spinnerArrayAdapter2);

        final ArrayAdapter<RefModel> spinnerArrayAdapter3 = new ArrayAdapter<RefModel>(context,
                R.layout.spinner_item, tipe);
        spinnerArrayAdapter3.setDropDownViewResource( R.layout.spinner_item );
        spTipe.setAdapter(spinnerArrayAdapter3);


        spTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tahunKendaraan = spTahun.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spMerk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                RefModel model = merk.get(position);
                if(model.getId()!=null){
                    merkKendaraan = model.getId();
                }
                else
                    merkKendaraan = "0";
                partsService.getTipeSearch(merkKendaraan, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject resultData = new JSONObject(result);
                            boolean error = resultData.getBoolean("error");
                            tipe.clear();
                            RefModel refModel = new RefModel();
                            refModel.setNama("Semua Tipe");
                            tipe.add(refModel);
                            if (!error) {
                                JSONArray dataArray = resultData.getJSONArray("content");
                                int sizeDataArray = dataArray.length();
                                for(int a=0;a<sizeDataArray;a++){
                                    JSONObject data = dataArray.getJSONObject(a);
                                    refModel = new RefModel(data.getString("id"), data.getString("nama"));
                                    tipe.add(refModel);
                                }

                            }
                            spinnerArrayAdapter3.notifyDataSetChanged();
                            int index = 0;
                            for(int a=0;a<tipe.size();a++){
                                RefModel model = tipe.get(a);
                                if(tipeInput.equals(model.getId()))
                                    index=a;
                            }
                            spTipe.setSelection(index);
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

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spTipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    RefModel model = tipe.get(position);
                    tipeKendaraan = model.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Nopol = inputNomorPolisi.getText().toString().trim();
                String kerusakan = inputKerusakan.getText().toString().trim();
                if(Nopol.equals("") || kerusakan.equals(""))
                    Toast.makeText(context,"Pastikan semua data terisi",Toast.LENGTH_SHORT).show();
                else{
                    if(tipeKendaraan==null)
                        tipeKendaraan = "0";
                    if(tahunKendaraan.equals("Semua Tahun"))
                        tahunKendaraan = "0";

                    jobService.setJobBayarJasa(session.getUserId(), session.getBengkelId(), jenis, merkKendaraan, tipeKendaraan, tahunKendaraan, Nopol, kerusakan, new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {

                            return null;
                        }

                        @Override
                        public String onError(VolleyError result) {
                            return null;
                        }
                    });
                    dialogError.dismiss();
                }
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
        partsService.getSearchMenu(jenis,1, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (!object.getBoolean("error")) {
                        JSONObject object1 = new JSONObject(object.getString("content"));
                        JSONArray arrMerk = new JSONArray(object1.getString("merk"));
                        merk.clear();
                        RefModel refModel = new RefModel();
                        refModel.setNama("Semua Jenis");
                        refModel.setNama("Semua Tipe");
                        tipe.add(refModel);
                        merk.add(refModel);
                        for (int a = 0; a < arrMerk.length(); a++) {
                            JSONObject data = arrMerk.getJSONObject(a);
                            refModel = new RefModel(data.getString("id"), data.getString("nama"));
                            merk.add(refModel);
                        }
                        spinnerArrayAdapter2.notifyDataSetChanged();
                        int index = 0;
                        for(int a=0;a<merk.size();a++){
                            RefModel model = merk.get(a);
                            if(merkInput.equals(model.getId()))
                                index=a;
                        }
                        spMerk.setSelection(index);
                        for(int a=0;a<tahun.size();a++){
                            RefModel model = tahun.get(a);
                            if(tahunInput.equals(model.getId()))
                                index=a;
                        }
                        spTahun.setSelection(index);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (dialogError != null && !dialogError.isShowing())
                    dialogError.show();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });


    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {


    }

    public void changeMap(double lat,double lng){
        if(mMap!=null){
            mMap.clear();
            LatLng lokasiSekarang = new LatLng(lat, lng);
            if(jenisBengkel.equals("1"))
                markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));
            else
                markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(lokasiSekarang);

            marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Customer").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
            builder.include(lokasi);

            final LatLngBounds bounds = builder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                }
            });
        }
    }
}
