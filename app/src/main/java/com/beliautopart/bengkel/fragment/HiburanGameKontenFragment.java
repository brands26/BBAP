package com.beliautopart.bengkel.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.NewsActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 03/07/2016.
 */
public class HiburanGameKontenFragment extends Fragment {
    private View v;
    private Activity context;
    private OrderService orderService;
    private SessionManager session;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private RelativeLayout btnBatal;
    private String nama;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LinearLayout vewList;
    private String url;
    private TextView txtJudul;
    private WebView txtIsi;
    private NetworkImageView imgIcon;
    private RelativeLayout lbtnSimpan;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_hiburan_detail_game, container, false);
        context = (Activity)getContext();
        orderService = new OrderService(context);
        session = new SessionManager(context);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        //txtTitle.setText("Order");
        txtJudul =(TextView) v.findViewById(R.id.txtJudul);
        content = getArguments().getString("content");
        nama = getArguments().getString("nama","");
        imgIcon = (NetworkImageView) v.findViewById(R.id.imgIcon);
        lbtnSimpan = (RelativeLayout) v.findViewById(R.id.lbtnSimpan);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtJudul.setTypeface(tf);
        txtIsi = (WebView) v.findViewById(R.id.txtIsi);

        if(!getArguments().getString("nama").equals(""))
            txtTitle.setText(nama);
        setUp(content);
        return v;
    }
    public void setUp(String content){
        if(!content.equals("")){
            try {
                final JSONObject obj = new JSONObject(content);
                txtJudul.setText(obj.getString("judul"));
                txtIsi.loadDataWithBaseURL(null, obj.getString("isi"), "text/html", "utf-8", null);
                url ="http://beliautopart.com/img/hiburan/"+obj.getString("sumber");
                imgIcon.setImageUrl(url,imageLoader);
                lbtnSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            startNewActivity(v.getContext(),obj.getString("vid"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ((NewsActivity)getActivity()).stopOnLoadingAnimation();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            ((NewsActivity)getActivity()).stopOnLoadingAnimation();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}