package com.beliautopart.bengkel.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.NewsActivity;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.webservices.HiburanService;
import com.beliautopart.bengkel.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 19/06/2016.
 */
public class HiburanListKontenFragment extends Fragment{
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
    private HiburanService hiburanService;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_hiburan_list_content, container, false);
        context = (Activity)getContext();
        hiburanService = new HiburanService(context);
        session = new SessionManager(context);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        //txtTitle.setText("Order");
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        vewList = (LinearLayout) v.findViewById(R.id.linierLayoutKategori);
        content = getArguments().getString("content");
        nama = getArguments().getString("nama","");
        if(!getArguments().getString("nama").equals(""))
            txtTitle.setText(nama);
        setUp(content);
        return v;
    }
    public void setUp(String content){
        if(!content.equals("")){
            try {
            JSONArray data = new JSONArray(content);
            int size = data.length();
            if(size>0){
                for (int a = 0; a < size; a++) {
                    JSONObject dataCart = data.getJSONObject(a);
                    try {
                        if(dataCart.getString("tipe").equals("1"))
                            addVewkategori(dataCart.getString("id"),dataCart.getString("judul"));
                        else if(dataCart.getString("tipe").equals("2"))
                            addVewVideo(dataCart.getString("id"),dataCart.getString("judul"),dataCart.getString("vid"));
                        else
                            addVewGame(dataCart.getString("id"),dataCart.getString("judul"),dataCart.getString("sumber"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            ((NewsActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }
        else{
            ((NewsActivity)getActivity()).stopOnLoadingAnimation();
        }
    }
    public void addVewkategori(final String id, final String nama){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_list_kontent_hiburan, null);
        final TextView txtNama = (TextView) child.findViewById(R.id.txtNama);
        NetworkImageView img = (NetworkImageView) child.findViewById(R.id.imgFile);
        url ="http://beliautopart.com/img/hiburan/read.png";
        img.setImageUrl(url,imageLoader);
        txtNama.setText(""+nama);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiburanService.GetDetail(id, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject no = new JSONObject(result);
                            if(!no.getBoolean("error")){
                                final JSONObject obj = new JSONObject(result);
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                HiburanDetailKonten hiburan = new HiburanDetailKonten();
                                Bundle bundle = new Bundle();
                                bundle.putString("content",obj.getString("content"));
                                bundle.putString("nama",nama);
                                hiburan.setArguments(bundle);
                                ft.replace(R.id.frameLayout, hiburan);
                                ft.addToBackStack(null);
                                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                                ft.commit();
                            }
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
                ((NewsActivity)getActivity()).startOnLoadingAnimation();

            }
        });
        vewList.addView(child);
    }
    public void addVewVideo(final String id, final String nama, final String vid){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_list_kontent_hiburan_video, null);
        final TextView txtNama = (TextView) child.findViewById(R.id.txtJudul);
        NetworkImageView img = (NetworkImageView) child.findViewById(R.id.imgFile);
        url ="http://i1.ytimg.com/vi/"+vid+"/default.jpg";
        img.setImageUrl(url,imageLoader);
        txtNama.setText(""+nama);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                HiburanPlayVideo hiburan = new HiburanPlayVideo();
                Bundle bundle = new Bundle();
                bundle.putString("vid",vid);
                hiburan.setArguments(bundle);
                ft.replace(R.id.frameLayout, hiburan);
                ft.addToBackStack(null);
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                ft.commit();


            }
        });
        vewList.addView(child);
    }
    public void addVewGame(final String id, final String judul, final String icon){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_list_kontent_hiburan_video, null);
        final TextView txtNama = (TextView) child.findViewById(R.id.txtJudul);
        NetworkImageView img = (NetworkImageView) child.findViewById(R.id.imgFile);
        url ="http://beliautopart.com/img/hiburan/"+icon;
        img.setImageUrl(url,imageLoader);
        txtNama.setText(""+judul);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiburanService.GetDetail(id, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject no = new JSONObject(result);
                            if(!no.getBoolean("error")){
                                final JSONObject obj = new JSONObject(result);
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                HiburanGameKontenFragment hiburan = new HiburanGameKontenFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("content",obj.getString("content"));
                                bundle.putString("nama",judul);
                                hiburan.setArguments(bundle);
                                ft.replace(R.id.frameLayout, hiburan);
                                ft.addToBackStack(null);
                                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                                ft.commit();
                            }
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
                ((NewsActivity)getActivity()).startOnLoadingAnimation();


            }
        });
        vewList.addView(child);
    }
    @Override
    public void onResume(){
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
