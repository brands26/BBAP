package com.beliautopart.bengkel.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.KomplainDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Brandon Pratama on 11/06/2016.
 */
public class KomplainDetailAdapter  extends RecyclerView.Adapter<KomplainDetailAdapter.MyViewHolder> {

    private final Activity context;
    private final SessionManager session;
    private List<KomplainDetailModel> itemList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNomor;
        private final TextView txtTanggal;
        private final TextView txtPesan;
        private final TextView txtDari;

        public MyViewHolder(View view) {
            super(view);
            txtNomor = (TextView) view.findViewById(R.id.txtNomor);
            txtDari = (TextView) view.findViewById(R.id.txtDari);
            txtTanggal = (TextView) view.findViewById(R.id.txtTanggal);
            txtPesan = (TextView) view.findViewById(R.id.txtPesan);
        }
    }


    public KomplainDetailAdapter(Context context, List<KomplainDetailModel> itemList) {
        this.context = (Activity) context;
        this.itemList = itemList;
        session = new SessionManager(context);
    }


    @Override
    public KomplainDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_list_komplain_detail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KomplainDetailAdapter.MyViewHolder holder, int position) {
        final KomplainDetailModel komplain = itemList.get(position);
        holder.txtNomor.setText(komplain.getNomor());
        if(session.getUserId().equals(komplain.getDari())){
            holder.txtDari.setText("Dari : Saya");
        }
        Date time = new java.util.Date(Long.parseLong(komplain.getTanggal())* (long) 1000);
        String vv = new SimpleDateFormat("HH:mm, EEE, d MMM yyyy").format(time);
        holder.txtTanggal.setText(vv);
        holder.txtPesan.setText(Html.fromHtml(komplain.getPesan()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
