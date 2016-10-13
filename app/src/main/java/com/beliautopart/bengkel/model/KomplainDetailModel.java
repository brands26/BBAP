package com.beliautopart.bengkel.model;

/**
 * Created by Brandon Pratama on 11/06/2016.
 */
public class KomplainDetailModel  {
    private String id;
    private String nomor;
    private String tanggal;
    private String pesan;
    private String dari;

    public KomplainDetailModel() {
    }

    public KomplainDetailModel(String id, String nomor, String tanggal, String pesan, String dari) {
        this.id = id;
        this.nomor = nomor;
        this.tanggal = tanggal;
        this.pesan = pesan;
        this.dari = dari;
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getDari() {
        return dari;
    }

    public void setDari(String dari) {
        this.dari = dari;
    }
}
