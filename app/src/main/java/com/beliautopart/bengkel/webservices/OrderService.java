package com.beliautopart.bengkel.webservices;

import android.content.Context;

import com.beliautopart.bengkel.app.AppConfig;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.OrderModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 20/05/16.
 */
public class OrderService {

    private final Context context;
    private final SessionManager session;
    private SendDataHelper sendData;
    private String response;

    public OrderService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
        session = new SessionManager(context);
    }

    public void setOrder(OrderModel order,String cart,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", order.getUserId());
        params.put("tujuankirim", order.getTujuanKirim());
        params.put("lat",""+order.getLat());
        params.put("lng", ""+order.getLng());
        params.put("alamat", order.getAlamat());
        params.put("cart", cart);
        params.put("jobid",order.getJobId());
        sendData.SendData(params, AppConfig.URL_ORDER, 0, callback);
    }
    public void getOrderAktif(String userId,int dialog,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_ORDERS_CEK_AKTIF, dialog, callback);
    }
    public void getOrderStatus(String orderId,int dialog,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", orderId);
        sendData.SendData(params, AppConfig.URL_ORDERS_STATUS, dialog, callback);
    }
    public void getOrderDetail(String orderId,int dialog,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", orderId);
        sendData.SendData(params, AppConfig.URL_ORDERS_DETAIL, dialog, callback);
    }
    public void setPembayaran(String orderId,String userId,int dialog,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", orderId);
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_ORDERS_PEMBAYARAN, dialog, callback);
    }

    public void getRekening(SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope", "nul");
        sendData.SendData(params, AppConfig.URL_ORDERS_REKENING, 0, callback);
    }
    public void getTime(String orderId,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", orderId);
        sendData.SendData(params, AppConfig.URL_ORDERS_WAKTU_PEMBAYARAN, 0, callback);
    }
    public void getBank(SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", session.getUserId());
        sendData.SendData(params, AppConfig.URL_ORDERS_BANK, 1, callback);
    }
    public void setKonfirmasiPembayaran(String id,String userId,int bank, int bankPerusahaan, String noRek,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("bank", ""+bank);
        params.put("bankperusahaan", ""+bankPerusahaan);
        params.put("norek", noRek);
        sendData.SendData(params, AppConfig.URL_ORDERS_KONFIRMASI, 0, callback);
    }
    public void getDetailVerifikasi(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_ORDERS_VERIFIKASI, 1, callback);
    }
    public void getBarangDiantar(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_ORDERS_BARANG_DIANTAR, 0, callback);
    }
    public void getDetailOrderKurir(String id,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_ORDERS_BARANG_DETAIL_KURIR, 0, callback);
    }
    public void setBarangDiterima(String id, String userId, String idKurir, String items, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("idKurir", idKurir);
        params.put("items", items);
        sendData.SendData(params, AppConfig.URL_ORDERS_BARANG_DITERIMA, 1, callback);
    }
    public void setBarangDireject(String id, String userId, String idKurir, String items,String salah,String cacat, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("idKurir", idKurir);
        params.put("items", items);
        params.put("salah", salah);
        params.put("cacat", cacat);
        sendData.SendData(params, AppConfig.URL_ORDERS_BARANG_DIREJECT, 1, callback);
    }

    public void getListOrder(String userId,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_ORDERS_LIST, 0, callback);
    }
    public void setBatal(String id,String userId,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_ORDERS_BATAL, 1, callback);
    }
    public void setKomplain(String id,String userId,String job, String komplain, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("job", job);
        params.put("komplain", komplain);
        sendData.SendData(params, AppConfig.URL_ORDERS_KOMPLAIN, 1, callback);
    }
    public void getKomplain(String userId, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        sendData.SendData(params, AppConfig.URL_ORDERS_KOMPLAIN_LIST, 0, callback);
    }
    public void getKomplainDetail(String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_ORDERS_KOMPLAIN_DETAIL, 0, callback);
    }
    public void getKurirLocation(String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_ORDERS_KURIR_LOCATION, 0, callback);
    }
    public void getHistoryDetail(String orderId,int dialog,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", orderId);
        sendData.SendData(params, AppConfig.URL_ORDERS_HISTORY_DETAIL, dialog, callback);
    }
    public void getHistoryJobDetail(String orderId,int dialog,SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", orderId);
        sendData.SendData(params, AppConfig.URL_ORDERS_HISTORY_JOB_DETAIL, dialog, callback);
    }

}
