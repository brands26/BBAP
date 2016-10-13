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
 * Created by brandon on 16/05/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<ItemProduk> itemList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNamaProduk, txtKode, txtJumlah, txtharga;
        public NetworkImageView imageProduk;

        public MyViewHolder(View view) {
            super(view);
            imageProduk = (NetworkImageView) view.findViewById(R.id.imgProduk);
            txtNamaProduk = (TextView) view.findViewById(R.id.txtNamaProduk);
            txtKode = (TextView) view.findViewById(R.id.txtKodeProduk);
            txtJumlah = (TextView) view.findViewById(R.id.txtJumlah);
            txtharga = (TextView) view.findViewById(R.id.txtHarga);
        }
    }


    public CartAdapter(List<ItemProduk> itemList) {
        this.itemList = itemList;
    }


    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartAdapter.MyViewHolder holder, int position) {
        ItemProduk itemProduk = itemList.get(position);
        holder.imageProduk.setImageUrl("http://beliautopart.com/_produk/thumbnail/" + itemProduk.getFoto().replace(" ", "%20"), imageLoader);
        holder.txtNamaProduk.setText(itemProduk.getNamaItem());
        holder.txtKode.setText(itemProduk.getKode());
        holder.txtJumlah.setText("Jumlah : "+itemProduk.getJumlah());
        int jumlahHarga = itemProduk.getJumlah()*itemProduk.getHarga();
        holder.txtharga.setText("Rp."+thousand(""+jumlahHarga));
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
