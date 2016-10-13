package com.beliautopart.bengkel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.fragment.OrderCartFragment;
import com.beliautopart.bengkel.fragment.OrderDetailFragment;
import com.beliautopart.bengkel.fragment.OrderPaymentWaitFragment;
import com.beliautopart.bengkel.fragment.OrderShipmentDoneFragment;
import com.beliautopart.bengkel.fragment.OrderShipmentFragment;
import com.beliautopart.bengkel.fragment.OrderShipmentRejectFragment;
import com.beliautopart.bengkel.fragment.OrderWaitFragment;
import com.beliautopart.bengkel.fragment.PaymentFragment;
import com.beliautopart.bengkel.fragment.PaymentVerivfikasiFragment;
import com.beliautopart.bengkel.helper.ProgressBarAnimation;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.EmptyStackException;

public class CartActivity extends AppCompatActivity {

    private SessionManager session;
    private Toolbar toolbar;
    private TextView txtTitle;
    private ImageButton btnback;
    private OrderService order;
    private CartActivity context;
    private RelativeLayout layoutLoading;
    private Handler handler;
    private OrderWaitFragment orderFragment;
    private String status = "";
    private OrderCartFragment cartFragment;
    private OrderDetailFragment orderDetail;
    private OrderPaymentWaitFragment paymentFragemnt;
    private PaymentFragment paymentFragment;
    private OrderPaymentWaitFragment paymentWaitFragment;
    private PaymentVerivfikasiFragment paymentverifikasiFragemnt;
    private OrderShipmentFragment shipmentFragment;
    private OrderShipmentDoneFragment shipmentDoneFragment;
    private OrderShipmentRejectFragment shipmentRejectFragment;
    private Dialog dialogStatus;
    private Animation rotation;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private Animation rotationA;
    private Animation rotationC;
    private String kat;
    private String OrderID = "";
    private TextView dialogStatusText;
    private Dialog dialogError;
    private String idIndent;
    private TextView progressText;
    private ProgressBar progressBar2;
    private int pro=0;
    private ProgressBarAnimation anim;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                kat = "1";
            } else {
                kat = extras.getString("kat");
                if(extras.getString("id")!=null){
                    idIndent = extras.getString("id");
                }
            }
        } else {
            kat = (String) savedInstanceState.getSerializable("kat");
        }

        context = this;
        session = new SessionManager(this);
        handler = new Handler();
        order = new OrderService(this);

        dialogStatus = new Dialog(context);
        dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStatus.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogStatus.setContentView(R.layout.dialog_status_berubah);
        dialogStatusText = (TextView) dialogStatus.findViewById(R.id.textView27);
        dialogStatus.setCancelable(true);
        Button btnOk = (Button) dialogStatus.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogStatus.dismiss();
            }
        });


        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert_order_aktif);
        dialogError.setCancelable(true);

        TextView txtStatus = (TextView) dialogError.findViewById(R.id.textView27);
        txtStatus.setText("Fitur sedang dalam pengembangan");
        Button dialogErrorbtnOk = (Button) dialogError.findViewById(R.id.btnOk);
        dialogErrorbtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);

        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnback.setVisibility(View.VISIBLE);
        txtTitle.setText("Shopping Cart");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        progressText = (TextView) findViewById(R.id.textView106);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.setProgress(0);
        startOnLoadingAnimation();
        if(idIndent!=null){
            session.setOrderAktif(true);
            session.setOrderId(idIndent);
        }
    }
    public void startOnLoadingAnimation(){
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopOnLoadingAnimation(){
        layoutLoading.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume(){
        super.onResume();
        order = new OrderService(this);
        startGetStatusOrder();

    }

    public Runnable orderStatusTread = new Runnable() {
        @Override
        public void run() {
            final String idOrder;
            if(session.getbengkelAktif()){
                idOrder =session.getBengkelIdOrder();
            }
            else{
                idOrder =session.getOrderId();
            }
            order.getOrderStatus(idOrder, 0, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);

                        // check for error
                        if (!obj.getBoolean("error")) {
                            session.setOrderStatus(obj.getString("content"));
                            if (obj.getString("content").equals("19")) {
                                finish();
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();
                                finish();
                            }else if (obj.getString("content").equals("98")) {
                                finish();
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();
                                finish();
                            }else if (obj.getString("content").equals("92")) {
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "ORDER TELAH DI-CANCEL OLEH SISTEM KERENA BATAS WAKTU PEMBAYARAN TELAH LEWAT", Toast.LENGTH_LONG).show();
                                finish();
                            }else if (obj.getString("content").equals("91")) {
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "ORDER TELAH DI-CANCEL OLEH SISTEM KERENA MELEBIHI BATAS WAKTU", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (obj.getString("content").equals("10")) {
                                if(!status.equals("waitOrder")){
                                    int progress = 25;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Langkah 1 dari 4");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getOrderDetail(idOrder, 0, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                orderFragment = new OrderWaitFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                orderFragment.setArguments(bundle);
                                                changeFragment(orderFragment);
                                                status = "waitOrder";
                                                stopOnLoadingAnimation();
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            } else if (obj.getString("content").equals("31")) {
                                if(!status.equals("OrderDetail")){
                                    int progress = 25;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Langkah 1 dari 4");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getOrderDetail(idOrder, 0, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                Log.d("data ",result);
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                orderDetail = new OrderDetailFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                orderDetail.setArguments(bundle);
                                                changeFragment(orderDetail);
                                                status = "OrderDetail";
                                                if(idIndent!=null){
                                                    orderDetail.SetStatus("Item yang anda pesan sudah tersedia, tekan bayar untuk membayar item dan melanjutkan order.");
                                                    orderDetail.indenLayoutGone();
                                                }
                                                stopOnLoadingAnimation();
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            } else if (obj.getString("content").equals("11")) {
                                if(!status.equals("payment")){
                                    int progress = 50;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Langkah 2 dari 4");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getOrderDetail(idOrder, 0, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                paymentWaitFragment = new OrderPaymentWaitFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                paymentWaitFragment.setArguments(bundle);
                                                changeFragment(paymentWaitFragment);
                                                status = "payment";
                                                startGetTime();
                                                stopOnLoadingAnimation();
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });

                                    stopGetStatusOrder();

                                }
                            } else if (obj.getString("content").equals("12")) {
                                if(!status.equals("paymentWait")){
                                    int progress = 50;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Langkah 2 dari 4");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getDetailVerifikasi(idOrder, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                paymentFragment = new PaymentFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                paymentFragment.setArguments(bundle);
                                                changeFragment(paymentFragment);
                                                status = "paymentWait";
                                                stopOnLoadingAnimation();
                                                dialogStatus("Silakan tunggu kami verifikasi pembayaran Anda.");
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            } else if (obj.getString("content").equals("33")) {
                                if(!status.equals("paymentverifikasi")){
                                    int progress = 50;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Langkah 2 dari 4");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getOrderDetail(idOrder,0, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                paymentverifikasiFragemnt = new PaymentVerivfikasiFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                paymentverifikasiFragemnt.setArguments(bundle);
                                                changeFragment(paymentverifikasiFragemnt);
                                                status = "paymentverifikasi";
                                                stopOnLoadingAnimation();
                                                dialogStatus("Pembayaran telah terverifikasi. Menunggu serah terima dengan kurir.");
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            } else if (obj.getString("content").equals("34")) {
                                if(!status.equals("shipment")){
                                    int progress = 75;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Langkah 3 dari 4");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getDetailOrderKurir(idOrder, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                shipmentFragment = new OrderShipmentFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                shipmentFragment.setArguments(bundle);
                                                changeFragment(shipmentFragment);
                                                status = "shipment";
                                                stopOnLoadingAnimation();
                                                dialogStatus("Silakan tunggu. Barang sedang dikirim ke lokasi Anda sekarang.");
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            } else if (obj.getString("content").equals("13")) {
                                if(!status.equals("shipmentDone")){
                                    int progress = 100;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Selesai");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getDetailOrderKurir(idOrder, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                shipmentDoneFragment = new OrderShipmentDoneFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                shipmentDoneFragment.setArguments(bundle);
                                                status = "shipmentDone";
                                                stopGetStatusOrder();
                                                changeFragment(shipmentDoneFragment);
                                                stopOnLoadingAnimation();
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            }else if (obj.getString("content").equals("14")) {
                                if(!status.equals("shipmentReject")){
                                    int progress = 100;
                                    anim = new ProgressBarAnimation(progressBar2, pro, progress);
                                    anim.setDuration(1000);
                                    progressText.setText("Selesai");
                                    progressBar2.startAnimation(anim);
                                    if(progress<pro){
                                        pro=pro+25;
                                    }
                                    order.getDetailOrderKurir(idOrder, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            AppController.getInstance().cancelPendingRequests("volley");
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                String content = obj.getString("content");
                                                shipmentRejectFragment = new OrderShipmentRejectFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content",content);
                                                bundle.putString("idOrder",idOrder);
                                                shipmentRejectFragment.setArguments(bundle);
                                                status = "shipmentReject";
                                                stopGetStatusOrder();
                                                changeFragment(shipmentRejectFragment);
                                                stopOnLoadingAnimation();
                                                return null;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return  result;
                                        }

                                        @Override
                                        public String onError(VolleyError result) {
                                            return null;
                                        }
                                    });
                                }
                            }
                        } else {
                            if(!status.equals("orderCart")){
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                cartFragment = new OrderCartFragment();
                                changeFragment(cartFragment);
                                stopOnLoadingAnimation();
                                status = "orderCart";
                            }
                        }
                    } catch (JSONException e) {
                    }
                    return result;
                }

                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });
            handler.postDelayed(orderStatusTread,2000);

        }

    };
    public Runnable getTimer = new Runnable() {
        @Override
        public void run() {
        order.getTime(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if(!obj.getBoolean("error")) {
                        String content = obj.getString("content");
                        paymentWaitFragment.setTime(content);
                    }
                    else{
                        stopGetTime();
                        startGetStatusOrder();
                    }
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
            handler.postDelayed(getTimer,1000);
        }

    };
    public void startGetStatusOrder(){
        handler.post(orderStatusTread);
    }
    public void stopGetStatusOrder(){
        handler.removeCallbacks(orderStatusTread);
    }
    public void startGetTime(){
        handler.post(getTimer);

    }

    public void stopGetTime(){
        handler.removeCallbacks(getTimer);
    }
    @Override
    public void onStop(){
        super.onStop();
        stopGetStatusOrder();
        stopGetTime();
    }


    public void startLoading(){
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopLoaing(){
        layoutLoading.setVisibility(View.GONE);
    }
    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void dialogStatus(String text){
        dialogStatusText.setText(text);
        if(!dialogStatus.isShowing())
            dialogStatus.show();

    }
    public void dialogStatus(){
        if(!dialogStatus.isShowing())
            dialogStatus.show();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopGetStatusOrder();
        stopGetTime();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public String getKat(){
        return kat;
    }

    public void dialogFitur(View v){
        if(!dialogError.isShowing())
            dialogError.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    if(imageReturnedIntent.getData()!=null){
                        try {
                            Uri selectedImage = imageReturnedIntent.getData();
                            InputStream imageStream = null;
                            String filename = selectedImage.getLastPathSegment()+".jpg";
                            imageStream = context.getContentResolver().openInputStream(selectedImage);
                            Bitmap fileBitmap = BitmapFactory.decodeStream(imageStream);
                            String file = "";
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            if(fileBitmap.getHeight() <= 600 && fileBitmap.getWidth() <= 600){
                                fileBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                                byte[] fileBitmapArray = baos.toByteArray();
                                file = Base64.encodeToString(fileBitmapArray, Base64.DEFAULT);
                            }
                            else{
                                float aspectRatio = fileBitmap.getWidth() /
                                        (float) fileBitmap.getHeight();

                                int width = 600;
                                int height = Math.round(width * aspectRatio);
                                fileBitmap = Bitmap.createScaledBitmap(fileBitmap, height, width, false);
                                fileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] fileBitmapArray = baos.toByteArray();
                                file = Base64.encodeToString(fileBitmapArray, Base64.DEFAULT);
                            }
                            shipmentFragment.setImageReject(fileBitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                }
                break;

            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK){
                    Log.d("set","gambar");
                    if(imageReturnedIntent!=null){
                        Log.d("set","gambar");
                        try {
                            String filename = "" +System.currentTimeMillis();
                            if(imageReturnedIntent.getExtras()!=null){
                                Bitmap fileBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                fileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] fileBitmapArray = baos.toByteArray();
                                String file = Base64.encodeToString(fileBitmapArray, Base64.DEFAULT);
                                Log.d("set","gambar");
                                shipmentFragment.setImageReject(fileBitmap);
                            }

                        } catch (EmptyStackException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    }


}
