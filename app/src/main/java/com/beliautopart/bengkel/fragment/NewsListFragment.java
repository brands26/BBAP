package com.beliautopart.bengkel.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
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
import com.beliautopart.bengkel.webservices.WebContentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 19/06/2016.
 */
public class NewsListFragment extends Fragment{
    private View v;
    private Activity context;
    private OrderService orderService;
    private SessionManager session;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private RelativeLayout btnBatal;
    private LinearLayout vewList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private HiburanService hiburanService;
    private String nama;
    private WebContentService webContentService;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_hiburan_kategori, container, false);
        context = (Activity)getContext();
        webContentService = new WebContentService(context);
        session = new SessionManager(context);
        vewList = (LinearLayout) v.findViewById(R.id.linierLayoutKategori);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        content = getArguments().getString("content");


        setUp(content);
        return v;
    }
    public void setUp(String content){
        try {
            JSONArray data = new JSONArray(content);
            int size = data.length();
            for (int a = 0; a < size; a++) {
                JSONObject dataCart = data.getJSONObject(a);
                try {
                    addVewkategori(dataCart.getString("id"),dataCart.getString("judul"),dataCart.getString("isi"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ((NewsActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addVewkategori(final String id, final String nama, String isi){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_list_kontent_news, null);
        final TextView txtNama = (TextView) child.findViewById(R.id.txtNama);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtNama.setTypeface(tf);

        final TextView txtIsi = (TextView) child.findViewById(R.id.txtIsi);
        NetworkImageView img = (NetworkImageView) child.findViewById(R.id.imgFile);
        String url ="http://bengkel.beliautopart.com/img/read.png";
        img.setImageUrl(url,imageLoader);
        txtIsi.setText(Html.fromHtml(isi));
        txtNama.setText(""+nama);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewsActivity)getActivity()).startOnLoadingAnimation();
                webContentService.getNewsDetail(id, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            final JSONObject res = new JSONObject(result);
                            if(!res.getBoolean("error")){
                                final JSONObject obj = new JSONObject(result);
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                HiburanDetailKonten hiburan = new HiburanDetailKonten();
                                Bundle bundle = new Bundle();
                                bundle.putString("content",obj.getString("content"));
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
