package com.beliautopart.bengkel.adapter;

/**
 * Created by brandon on 26/05/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.MessageModel;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private SessionManager sessionHelper;
    private Context mContext;
    private ArrayList<MessageModel> chatArrayList;
    private int SELF_MSG = 100;
    private int YOUR_MSG = 200;
    private int SELF_IMG = 300;
    private int YOUR_IMG = 400;
    private int INFO = 3;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNama;
        private final TextView txtWaktu;
        private final NetworkImageView image;
        private final TextView txtRead;
        public TextView txtPesan;
    public ViewHolder(View view) {
        super(view);
        txtPesan = (TextView) view.findViewById(R.id.txtPesan);
        txtNama = (TextView) view.findViewById(R.id.txtNama);
        txtWaktu = (TextView) view.findViewById(R.id.txtWaktu);
        image = (NetworkImageView) view.findViewById(R.id.image);
        txtRead = (TextView) view.findViewById(R.id.txtRead);

    }
}


    @Override
    public int getItemViewType(int position) {
        MessageModel chat = chatArrayList.get(position);
        if(chat.getId().equals("3")){
            return INFO;
        }
        else if (chat.getId().equals(sessionHelper.getUserId()) && chat.isFile()) {
            return SELF_IMG;
        }
        else if(chat.getId().equals(sessionHelper.getUserId()) && !chat.isFile()){
            return SELF_MSG;
        }
        else if (!chat.getId().equals(sessionHelper.getUserId()) && chat.isFile()) {
            return YOUR_IMG;
        }
        else if(!chat.getId().equals(sessionHelper.getUserId()) && !chat.isFile()){
            return YOUR_MSG;
        }

        return position;
    }


    public MessageAdapter(Context mContext, ArrayList<MessageModel> chatArrayList) {
        this.mContext = mContext;
        this.chatArrayList = chatArrayList;
        sessionHelper = new SessionManager(mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageModel chat = chatArrayList.get(position);
        byte[] data = Base64.decode(chat.getMsg(), Base64.DEFAULT);
        try {
            String text = new String(data, "UTF-8");

            Date time = new Date(Long.parseLong(chat.getWaktu())* (long) 1000);
            String vv = new SimpleDateFormat("EEE,HH:mm").format(time);
            holder.txtWaktu.setText(vv);
            holder.txtPesan.setText(text);
            if(!sessionHelper.getUserId().equals(chat.getId())){
                holder.txtRead.setVisibility(View.GONE);
                holder.txtNama.setText(sessionHelper.getNamaUserJob());
            }
            else{
                holder.txtRead.setVisibility(View.VISIBLE);
                holder.txtNama.setText(sessionHelper.getUserNama());
                if(chat.isRead())
                    holder.txtRead.setText("R");
                else if(!chat.isSending() && !chat.isSent())
                    holder.txtRead.setText("S");
                else if(!chat.isSending() && chat.isSent())
                    holder.txtRead.setText("D");
                else if(chat.isSending())
                    holder.txtRead.setText("sending");

            }
            if(chat.isFile()){
                holder.image.setImageUrl("http://beliautopart.com/~uploads/"+text.replaceAll(" ", "%20"),imageLoader);
                /*
                ImageLoader.ImageContainer container = (ImageLoader.ImageContainer) holder.image.getTag();
                final Bitmap bitmap = container.getBitmap();
                if (bitmap != null) {
                    touch.setImageBitmap(bitmap);
                    layout.addView(touch);
                }
                */
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        if (viewType == SELF_MSG) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_right_row, parent, false);
        }
        else if (viewType == SELF_IMG) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_right_image__row, parent, false);
        }
        else if (viewType == YOUR_MSG) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_left_row, parent, false);
        }
        else if (viewType == YOUR_IMG) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_left_image_row, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }
}