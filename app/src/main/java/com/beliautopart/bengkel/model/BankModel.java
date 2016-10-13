package com.beliautopart.bengkel.model;

/**
 * Created by brandon on 23/05/16.
 */
public class BankModel {
    private int id;
    private String nama;


    public BankModel(int id, String nama) {

        this.id = id;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
