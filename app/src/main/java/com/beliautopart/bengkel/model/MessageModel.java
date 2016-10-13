package com.beliautopart.bengkel.model;

/**
 * Created by brandon on 26/05/16.
 */
public class MessageModel {
    private String id;
    private String msg;
    private String nama;
    private String waktu;
    private boolean file;
    private boolean read;
    private boolean sending;
    private boolean sent;

    public MessageModel(String id, String msg, String nama, String waktu, boolean file, boolean read) {
        this.id = id;
        this.msg = msg;
        this.nama = nama;
        this.waktu = waktu;
        this.file = file;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }
}
