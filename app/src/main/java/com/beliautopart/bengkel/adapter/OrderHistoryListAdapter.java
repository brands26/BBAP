package com.beliautopart.bengkel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.DetailHistoryOrderActivity;
import com.beliautopart.bengkel.activity.DetailJobHistoryOrderActivity;
import com.beliautopart.bengkel.activity.KomplainActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.model.OrderListModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by brandon on 28/05/16.
 */
public class OrderHistoryListAdapter extends RecyclerView.Adapter<OrderHistoryListAdapter.MyViewHolder> {

    private  Activity context;
    private List<OrderListModel> itemList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton btnDetail;
        private final ImageButton btnKomplain;
        public TextView txtNoId, txtTimeSpan, txtStatusOrder, txtJumlah;
        public NetworkImageView imageProduk;

        public MyViewHolder(View view) {
            super(view);
            txtNoId = (TextView) view.findViewById(R.id.txtNomor);
            txtTimeSpan = (TextView) view.findViewById(R.id.txtTimeSpan);
            txtStatusOrder = (TextView) view.findViewById(R.id.txtStatusOrder);
            txtJumlah = (TextView) view.findViewById(R.id.txtBiaya);
            btnDetail = (ImageButton) view.findViewById(R.id.btnDetail);
            btnKomplain = (ImageButton) view.findViewById(R.id.btnKomplain);

        }
    }


    public OrderHistoryListAdapter(Context context, List<OrderListModel> itemList) {
        this.context =(Activity) context;
        this.itemList = itemList;
    }


    @Override
    public OrderHistoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderHistoryListAdapter.MyViewHolder holder, int position) {
        final OrderListModel orderListModel = itemList.get(position);
        holder.txtNoId.setText(orderListModel.getNomor());
        Date time = new Date(Long.parseLong(orderListModel.getTanggal())* (long) 1000);
        String vv = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(time);
        holder.txtTimeSpan.setText(vv);
        if(orderListModel.getStatus_date().equalsIgnoreCase("Barang Diterima") || orderListModel.getStatus_date().equalsIgnoreCase("job done"))
            holder.txtStatusOrder.setBackgroundResource(R.drawable.bg_green);
        else
            holder.txtStatusOrder.setBackgroundResource(R.drawable.bg_red);
        holder.txtStatusOrder.setText(orderListModel.getStatus_date());
        String jumlahharga = orderListModel.getTotalHarga();
        String[] separator;
        String delimiter ="\\.";
        if(jumlahharga.contains(".")){
            separator = jumlahharga.split(delimiter);
            jumlahharga = separator[0];
        }
        if(!orderListModel.getTotalHarga().equals("null"))
            holder.txtJumlah.setText("Rp"+thousand(jumlahharga));
        else{
            holder.txtJumlah.setVisibility(View.INVISIBLE);
        }
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!orderListModel.getJenis().equalsIgnoreCase("job")){
                    Intent i = new Intent(context.getApplicationContext(),DetailHistoryOrderActivity.class);
                    i.putExtra("idOrder",orderListModel.getId());
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else{
                    Intent i = new Intent(context.getApplicationContext(),DetailJobHistoryOrderActivity.class);
                    i.putExtra("idJob",orderListModel.getId());
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            }
        });
        holder.btnKomplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderListModel.getJenis().equalsIgnoreCase("job")){
                    Intent i = new Intent(context.getApplicationContext(),KomplainActivity.class);
                    i.putExtra("id",orderListModel.getId());
                    i.putExtra("status","job");
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else{
                    Intent i = new Intent(context.getApplicationContext(),KomplainActivity.class);
                    i.putExtra("id",orderListModel.getId());
                    i.putExtra("status","order");
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }
    private String getDate(String time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }
    private String thousand(String number){
        StringBuilder strB = new StringBuilder();
        strB.append(number);
        int Three = 0;

        for(int i=number.length();i>0;i--){
            Three++;
            if(Three == 3){
                strB.insert(i-1, ".");
                Three = 0;
            }
        }
        return strB.toString();
    }
}

