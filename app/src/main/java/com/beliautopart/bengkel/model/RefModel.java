package com.beliautopart.bengkel.model;

/**
 * Created by Brandon Pratama on 14/06/2016.
 */
public class RefModel {
    private String id;
    private String nama;

    public RefModel(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public RefModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
    @Override
    public String toString() {
        return this.nama;
    }
}
