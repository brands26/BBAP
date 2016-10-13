package com.beliautopart.bengkel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.model.ItemProduk;

import java.util.List;

/**
 * Created by brandon on 13/05/16.
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {

    private List<ItemProduk> itemList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNamaProduk, txtKode, txtKompatibel, txtharga;
        public NetworkImageView imageProduk;

        public MyViewHolder(View view) {
            super(view);
            imageProduk = (NetworkImageView) view.findViewById(R.id.imgProduk);
            txtNamaProduk = (TextView) view.findViewById(R.id.txtNamaProduk);
            //txtKode = (TextView) view.findViewById(R.id.txtKode);
            //txtKompatibel = (TextView) view.findViewById(R.id.txtKompatibel);
            //txtharga = (TextView) view.findViewById(R.id.txtharga);
        }
    }


    public ItemListAdapter(List<ItemProduk> itemList) {
        this.itemList = itemList;
    }


    @Override
    public ItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemListAdapter.MyViewHolder holder, int position) {
        ItemProduk itemProduk = itemList.get(position);
        holder.imageProduk.setDefaultImageResId(R.drawable.satellite);
        holder.imageProduk.setImageUrl("http://beliautopart.com/_produk/thumbnail/" + itemProduk.getFoto().replace(" ", "%20"), imageLoader);
        holder.txtNamaProduk.setText(itemProduk.getNamaItem()+"\n"+itemProduk.getKode()+"\n"+itemProduk.getMade());
        //holder.txtKode.setText(itemProduk.getKode());
        //holder.txtKompatibel.setText(itemProduk.getKompatibel());
        //holder.txtharga.setText("" + itemProduk.getHarga());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
