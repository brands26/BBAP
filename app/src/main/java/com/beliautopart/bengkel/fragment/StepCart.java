package com.beliautopart.bengkel.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.adapter.CartAdapter;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon on 17/05/16.
 */
public class StepCart extends Fragment {

    private int i = 1;
    private SessionManager session;
    private View v;
    private boolean setuju=false;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.step_cart, container, false);
        session =new SessionManager(getContext());

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public void onResume(){
        super.onResume();

        CartAdapter cartAdapter = null;
        final List<ItemProduk> cartProdukList;
        if(session.getCart()!=null)
            cartProdukList = session.getCart();
        else
            cartProdukList= new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        cartAdapter = new CartAdapter(cartProdukList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cartAdapter);

        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Alert message to be shown");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setuju=true;
                        dialog.dismiss();
                    }
                });

    }
}

