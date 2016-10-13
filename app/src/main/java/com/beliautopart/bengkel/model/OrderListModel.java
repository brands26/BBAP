package com.beliautopart.bengkel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon on 27/05/16.
 */
public class OrderListModel {
    private String id;
    private String jenis;
    private String nomor;
    private String tanggal;
    private String status_date;
    private List<ItemProduk> itemProdukList =  new ArrayList<>();
    private String totalHarga;

    public OrderListModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getStatus_date() {
        return status_date;
    }

    public void setStatus_date(String status_date) {
        this.status_date = status_date;
    }

    public List<ItemProduk> getItemProdukList() {
        return itemProdukList;
    }

    public void addItemProdukList(ItemProduk itemProduk) {
        this.itemProdukList.add(itemProduk);
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public void setItemProdukList(List<ItemProduk> itemProdukList) {
        this.itemProdukList = itemProdukList;
    }
}
