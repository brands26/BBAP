package com.beliautopart.bengkel.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.JobAmbilActivity;
import com.beliautopart.bengkel.activity.JobDetailActivity;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.JobModel;
import com.beliautopart.bengkel.webservices.JobService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Brandon Pratama on 12/07/2016.
 */
public class JobAdapter extends RecyclerView.Adapter<JobAdapter.MyViewHolder> {

    private final SessionManager session;
    private final Dialog dialogError;
    private List<JobModel> moviesList;
    private Activity context;
    private JobService jobService;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tipe;
        private final TextView jenisJob;
        public TextView id,nama,jarak,waktu,kendaraan,nopol,kerusakan;
        public Button btnAmbil, btnDetail;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.txtJobId);
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "fonts/UbuntuCondensed-Regular.ttf");
            id.setTypeface(tf);
            nama = (TextView) view.findViewById(R.id.txtNamaCustomer);
            jarak = (TextView) view.findViewById(R.id.txtJarak);
            waktu = (TextView) view.findViewById(R.id.txtWaktu);
            kendaraan = (TextView) view.findViewById(R.id.txtKendaraan);
            tipe = (TextView) view.findViewById(R.id.txtTipe);
            nopol = (TextView) view.findViewById(R.id.txtNopol);
            kerusakan = (TextView) view.findViewById(R.id.txtKerusakan);
            jenisJob = (TextView) view.findViewById(R.id.txtJenisJob);
            btnDetail = (Button) view.findViewById(R.id.btnDetail);
            btnAmbil = (Button) view.findViewById(R.id.btnAmbil);
        }
    }


    public JobAdapter(List<JobModel> moviesList,Context context) {
        this.moviesList = moviesList;
        this.context = (Activity)context;
        jobService = new JobService(context);
        session = new SessionManager(context);
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert_order_aktif);
        TextView dialogText = (TextView) dialogError.findViewById(R.id.textView27);
        dialogText.setText("Maaf, job ini sudah tidak tersedia lagi.");
        Button dialogErrorButton = (Button) dialogError.findViewById(R.id.btnOk);
        dialogErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
        dialogError.setCancelable(false);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_adapter_job_request, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final JobModel movie = moviesList.get(position);
        holder.id.setText("Job ID : "+movie.getId());
        holder.nama.setText(movie.getNama());
        holder.jarak.setText(movie.getDistance());
        holder.waktu.setText(movie.getWaktu());
        holder.kendaraan.setText(movie.getMerk());
        holder.tipe.setText(movie.getTipe());
        holder.nopol.setText(movie.getNopol());
        holder.kerusakan.setText(movie.getDescription());
        holder.jenisJob.setText(movie.getJenis());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, JobDetailActivity.class);
                i.putExtra("id", movie.getId());
                context.startActivity(i);
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        holder.btnAmbil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobService.setAmbilJob(session.getUserId(), movie.getId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject data = new JSONObject(result);
                            if(!data.getBoolean("error")){

                                session.setBengkelAktif(true);
                                session.setBengkelId(movie.getId());
                                session.setBengkelUserId(movie.getUserId());
                                Intent i = new Intent(context, JobAmbilActivity.class);
                                context.startActivity(i);
                                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }

                            else{
                                if(!dialogError.isShowing())
                                    dialogError.show();
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
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
