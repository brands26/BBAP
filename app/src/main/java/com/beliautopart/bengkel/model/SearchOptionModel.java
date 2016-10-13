package com.beliautopart.bengkel.model;

/**
 * Created by brandon on 12/05/16.
 */
public class SearchOptionModel {
    private String keyword = "";
    private String kat = null;
    private String jenis = null;
    private String merk = null;
    private String tipe = null;
    private String katItem = null;
    private String lat = "0";
    private String lng ="0";
    private String page ="0";

    public SearchOptionModel() {
    }

    public SearchOptionModel(String keyword, String kat, String jenis, String merk, String tipe, String katItem) {
        this.keyword = keyword;
        this.kat = kat;
        this.jenis = jenis;
        this.merk = merk;
        this.tipe = tipe;
        this.katItem = katItem;
    }

    public SearchOptionModel(String keyword, String kat, String jenis, String merk, String tipe, String katItem, String lat, String lng) {
        this.keyword = keyword;
        this.kat = kat;
        this.jenis = jenis;
        this.merk = merk;
        this.tipe = tipe;
        this.katItem = katItem;
        this.lat = lat;
        this.lng = lng;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKat() {
        return kat;
    }

    public void setKat(String kat) {
        this.kat = kat;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getKatItem() {
        return katItem;
    }

    public void setKatItem(String katItem) {
        this.katItem = katItem;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
