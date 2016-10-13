package com.beliautopart.bengkel.app;

/**
 * Created by brandon on 12/05/16.
 */
public class AppConfig {
    public static String SERVER = "http://api.beliautopart.com/";
    public static String URL_PARTS_GET = SERVER + "parts.php?action=get";
    public static String URL_PARTS_SEARCH = SERVER + "parts.php?action=getSearchMenu";
    public static String URL_PARTS_TIPE = SERVER + "parts.php?action=getTipeSearch";

    public static String URL_USER_REGISTER = SERVER + "user.php?action=bengkelregister";
    public static String URL_USER_LOGIN = SERVER + "user.php?action=bengkellogin";
    public static String URL_USER_UPDATE = SERVER + "user.php?action=updateBengkelUser";
    public static String URL_USER_PROFILE = SERVER + "user.php?action=getBengkelProfile";
    public static String URL_USER_KAB = SERVER + "user.php?action=getKab";
    public static String URL_USER_GET_PASSWORD = SERVER + "user.php?action=getBengkelPassword";

    public static String URL_ORDER = SERVER + "order.php?action=newBengkelOrder";
    public static String URL_ORDERS_STATUS = SERVER + "order.php?action=getBengkelStatus";
    public static String URL_ORDERS_CEK_AKTIF = SERVER + "order.php?action=getBengkelOrderAktif";
    public static String URL_ORDERS_DETAIL = SERVER + "order.php?action=getBengkelDetail";
    public static String URL_ORDERS_PEMBAYARAN = SERVER + "order.php?action=setBengkelPembayaran";
    public static String URL_ORDERS_WAKTU_PEMBAYARAN = SERVER + "order.php?action=getBengkelTimePembayaran";
    public static String URL_ORDERS_REKENING = SERVER + "order.php?action=getRekening";
    public static String URL_ORDERS_BANK = SERVER + "order.php?action=getBengkelBank";
    public static String URL_ORDERS_KONFIRMASI = SERVER + "order.php?action=setBengkelKonfirmasiPembayaran";
    public static String URL_ORDERS_VERIFIKASI = SERVER + "order.php?action=getBengkelDetailVerifikasi";
    public static String URL_ORDERS_BARANG_DIANTAR = SERVER + "order.php?action=getBengkelDetailBarangDiantar";
    public static String URL_ORDERS_BARANG_DETAIL_KURIR = SERVER + "order.php?action=getBengkelDetailOrderKurir";
    public static String URL_ORDERS_BARANG_DITERIMA = SERVER + "order.php?action=setBengkelBarangDiterima";
    public static String URL_ORDERS_BARANG_DIREJECT = SERVER + "order.php?action=setBengkelBarangDireject";
    public static String URL_ORDERS_LIST = SERVER + "order.php?action=getBengkelListOrder";
    public static String URL_ORDERS_BATAL = SERVER + "order.php?action=setBengkelBatal";
    public static String URL_ORDERS_KOMPLAIN = SERVER + "order.php?action=setBengkelKomplain";
    public static String URL_ORDERS_KOMPLAIN_LIST = SERVER + "order.php?action=getKomplain";
    public static String URL_ORDERS_KOMPLAIN_DETAIL = SERVER + "order.php?action=getKomplainDetail";
    public static String URL_ORDERS_KURIR_LOCATION = SERVER + "order.php?action=getBengkelKurirLocation" ;
    public static String URL_ORDERS_HISTORY_DETAIL = SERVER + "order.php?action=getBengkelHistoryDetail";
    public static String URL_ORDERS_HISTORY_JOB_DETAIL = SERVER + "order.php?action=getBengkelHistoryJobDetail" ;



    public static String URL_JOB_AVAIABLE = SERVER + "bengkel.php?action=getJobAvaiable";
    public static String URL_JOB_AMBIL = SERVER + "bengkel.php?action=setAmbilJob";
    public static String URL_JOB_TOTAL_AVAIABLE =SERVER + "bengkel.php?action=getTotalJobAvaiable";
    public static String URL_BENGKEL_JOB_DETAIL = SERVER + "bengkel.php?action=getDetailJobBengkel";
    public static String URL_BENGKEL_JOB_STATUS = SERVER + "bengkel.php?action=getJobDetail";
    public static String URL_BENGKEL_JOB_BAYAR = SERVER + "bengkel.php?action=setJobBayarJasa";
    public static String URL_BENGKEL_SEND_CHAT = SERVER + "bengkel.php?action=sendMessage";
    public static String URL_BENGKEL_SEND_CHAT_IMAGE = SERVER + "bengkel.php?action=sendImageMessage";
    public static String URL_BENGKEL_READ_CHAT = SERVER + "bengkel.php?action=getBengkelMessage";

    public static String URL_BENGKEL_LOCATION = SERVER + "bengkel.php?action=getBengkelLocation";
    public static String URL_BENGKEL_JOB = SERVER + "bengkel.php?action=setJobBengkel";
    public static String URL_BENGKEL_JOB_READY_BAYAR = SERVER + "bengkel.php?action=setReadyBayar";
    public static String URL_BENGKEL_JOB_DETAIL_USER = SERVER + "bengkel.php?action=getJobBengkelDetail";
    public static String URL_BENGKEL_JOB_DONE = SERVER + "bengkel.php?action=setJobBengkelDone";
    public static String URL_BENGKEL_SET_PEMBAYARAN = SERVER + "bengkel.php?action=setPembayaranJob";
    public static String URL_BENGKEL_SET_PEMBAYARAN_BIAYA = SERVER + "bengkel.php?action=setPembayaranBiaya";
    public static String URL_BENGKEL_BATAL = SERVER + "bengkel.php?action=setPembatalanJob";
    public static  String URL_JOB_SETPOSISI = SERVER + "bengkel.php?action=setPosisi";
    public static  String URL_JOB_SETLOKASI = SERVER + "bengkel.php?action=setLokasi";

    public static String URL_HIBURAN_KATEGORI = SERVER + "hiburan.php?action=getHiburan";
    public static String URL_HIBURAN_FOLDER = SERVER + "hiburan.php?action=cekFolder";
    public static String URL_HIBURAN_KONTENT = SERVER + "hiburan.php?action=getHiburanKonten";
    public static String URL_HIBURAN_DETAIL = SERVER + "hiburan.php?action=getHiburanDetail";

    public static String URL_WEBKONTENT_ABOUT = SERVER + "webcontent.php?action=aboutus";
    public static String URL_WEBKONTENT_NEWS = SERVER + "webcontent.php?action=getNews";
    public static String URL_WEBKONTENT_NEWS_DETAIL = SERVER + "webcontent.php?action=getNewsDetail";
    public static String URL_WEBKONTENT_SETTING = SERVER +  "webcontent.php?action=load_setting";

}
