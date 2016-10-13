package com.beliautopart.bengkel.model;

/**
 * Created by brandon on 20/05/16.
 */
public class OrderModel {
    private String userId;
    private String tujuanKirim;
    private double lat;
    private double lng;
    private String alamat;
    private String jobId;

    public OrderModel(String userId, String tujuanKirim, double lat, double lng, String alamat, String jobId) {
        this.userId = userId;
        this.tujuanKirim = tujuanKirim;
        this.lat = lat;
        this.lng = lng;
        this.alamat = alamat;
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTujuanKirim() {
        return tujuanKirim;
    }

    public void setTujuanKirim(String tujuanKirim) {
        this.tujuanKirim = tujuanKirim;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
