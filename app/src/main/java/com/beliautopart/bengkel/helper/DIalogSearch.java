package com.beliautopart.bengkel.helper;

/**
 * Created by Brands Pratama on 7/26/2016.
 */
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.SearchActivity;
import com.beliautopart.bengkel.model.RefModel;
import com.beliautopart.bengkel.model.SearchOptionModel;
import com.beliautopart.bengkel.webservices.PartsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DIalogSearch extends DialogFragment {
    public String kategori;
    public int Jenis;
    private DialogSearchkategori dialogSearchkategori;
    private DialogSearchJenis dialogSearchJenis;
    private DialogSearchMerk dialogSearchmerk;
    private PartsService partsService;
    private List<RefModel> merk = new ArrayList<>();
    private List<RefModel> tipe = new ArrayList<>();
    private String merkKendaraan=null;
    private String tipeKendaraan=null;
    private String jenis=null;
    private DialogSearchTipe dialogSearchtipe;
    private DialogSearchParts dialogSearchparts;
    private SessionManager session;
    private RelativeLayout searchLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_search_item, container,
                false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        searchLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout27);


        ImageView btnBack = (ImageView) rootView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 1) {
                    Log.i("MainActivity", "popping backstack");
                    fm.popBackStack();
                }
                else {
                    getDialog().dismiss();
                }
            }
        });
        final EditText SearchInput = (EditText) rootView.findViewById(R.id.SearchInput);
        SearchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    SearchInput.setTypeface(null, Typeface.NORMAL);
                }
                else
                {
                    SearchInput.setTypeface(null, Typeface.ITALIC);
                }
            }
        });
        SearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String kat = ((SearchActivity)getActivity()).getKat();
                    SearchOptionModel searchOption = new SearchOptionModel();
                    searchOption.setKat(kat);
                    searchOption.setKeyword(SearchInput.getText().toString().trim());
                    ((SearchActivity)getActivity()).startOnLoadingAnimation();
                    ((SearchActivity)getActivity()).onSearch(searchOption);
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });
        RelativeLayout btnSearch = (RelativeLayout) rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = SearchInput.getText().toString().trim();
                if(!key.equals("")){
                    String kat = ((SearchActivity)getActivity()).getKat();
                    SearchOptionModel searchOption = new SearchOptionModel();
                    searchOption.setKat(kat);
                    searchOption.setKeyword(key);
                    ((SearchActivity)getActivity()).startOnLoadingAnimation();
                    ((SearchActivity)getActivity()).onSearch(searchOption);
                    getDialog().dismiss();
                }
                else{
                    Toast.makeText(getContext(),"Masukkan nama Barang yang anda butuhkan",Toast.LENGTH_SHORT).show();
                }
            }
        });
        session = new SessionManager(getContext());
        dialogSearchparts = new DialogSearchParts();
        dialogSearchkategori = new DialogSearchkategori();
        dialogSearchJenis = new DialogSearchJenis();
        dialogSearchmerk = new DialogSearchMerk();
        dialogSearchtipe = new DialogSearchTipe();
        partsService = new PartsService(getContext());
        changeFragment(dialogSearchparts);
        // Do something else
        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        rootView.setMinimumWidth((int)(displayRectangle.width() * 0.8f));
        return rootView;
    }
    private void changeFragment(Fragment fragment) {
        if(!fragment.isAdded()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    private void dialogHide(){
        getDialog().dismiss();
    }
    @SuppressLint("ValidFragment")
    public class DialogSearchParts extends Fragment {
        private View v;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            searchLayout.setVisibility(View.GONE);

            v = inflater.inflate(R.layout.fragment_dialog_search_parts, container, false);
            RelativeLayout lspare = (RelativeLayout) v.findViewById(R.id.lspare);
            RelativeLayout laks = (RelativeLayout) v.findViewById(R.id.laks);
            lspare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchActivity)getActivity()).setkat("2");
                    changeFragment(dialogSearchmerk);
                }
            });
            laks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchActivity)getActivity()).setkat("1");
                    changeFragment(dialogSearchmerk);
                }
            });
            return v;
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogSearchkategori extends Fragment {
        private View v;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            searchLayout.setVisibility(View.VISIBLE);

            v = inflater.inflate(R.layout.fragment_dialog_search_kategori, container, false);

            RelativeLayout lspare = (RelativeLayout) v.findViewById(R.id.lspare);
            RelativeLayout laks = (RelativeLayout) v.findViewById(R.id.laks);
            lspare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String kategori = "1";
                    ((SearchActivity)getActivity()).setkategori(kategori);
                    FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frameLayout, dialogSearchJenis);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            laks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String kategori = "2";
                    ((SearchActivity)getActivity()).setkategori(kategori);
                    FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frameLayout, dialogSearchJenis);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            return v;
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogSearchJenis extends Fragment {
        private View v;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            searchLayout.setVisibility(View.VISIBLE);
            v = inflater.inflate(R.layout.fragment_dialog_search_jenis, container, false);
            RelativeLayout lspare = (RelativeLayout) v.findViewById(R.id.lspare);
            RelativeLayout laks = (RelativeLayout) v.findViewById(R.id.laks);
            session = new SessionManager(getContext());
            lspare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jenis = "2";


                    SearchOptionModel searchOption = new SearchOptionModel();

                    String kat = ((SearchActivity)getActivity()).getKat();
                    String kategori = ((SearchActivity)getActivity()).getkategori();
                    String merkKendaraan = ((SearchActivity)getActivity()).getmerkKendaraan();
                    String tipeKendaraan = ((SearchActivity)getActivity()).gettipeKendaraan();

                    searchOption.setJenis(jenis);
                    searchOption.setKatItem(kategori);
                    searchOption.setMerk(merkKendaraan);
                    searchOption.setKat(kat);
                    searchOption.setTipe(tipeKendaraan);
                    searchOption.setLat(""+session.getUserlat());
                    searchOption.setLng(""+session.getUserlng());
                    ((SearchActivity)getActivity()).startOnLoadingAnimation();
                    ((SearchActivity)getActivity()).onSearch(searchOption);
                    dialogHide();
                }
            });
            laks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jenis = "1";


                    SearchOptionModel searchOption = new SearchOptionModel();


                    String kat = ((SearchActivity)getActivity()).getKat();
                    String kategori = ((SearchActivity)getActivity()).getkategori();
                    String merkKendaraan = ((SearchActivity)getActivity()).getmerkKendaraan();
                    String tipeKendaraan = ((SearchActivity)getActivity()).gettipeKendaraan();

                    searchOption.setJenis(jenis);
                    searchOption.setKatItem(kategori);
                    searchOption.setMerk(merkKendaraan.equals("0")?null:merkKendaraan);
                    searchOption.setKat(kat);
                    searchOption.setTipe(tipeKendaraan.equals("0")?null:tipeKendaraan);
                    searchOption.setLat("" + session.getUserlat());
                    searchOption.setLng("" + session.getUserlng());
                    ((SearchActivity) getActivity()).startOnLoadingAnimation();
                    ((SearchActivity) getActivity()).onSearch(searchOption);
                    dialogHide();
                }
            });
            return v;
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogSearchMerk extends Fragment {
        private View v;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_dialog_search_merk, container, false);
            partsService = new PartsService(getContext());
            String kat = ((SearchActivity)getActivity()).getKat();

            ListView listView = (ListView) v.findViewById(R.id.list);
            dialogSearchkategori = new DialogSearchkategori();
            dialogSearchtipe = new DialogSearchTipe();

            final ArrayAdapter<RefModel> adapter = new ArrayAdapter<RefModel>(getContext(), android.R.layout.simple_list_item_1,merk);
            listView.setAdapter(adapter);

            partsService.getSearchMenu(kat,0, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if(!object.getBoolean("error")){
                            JSONObject object1 = new JSONObject(object.getString("content"));
                            JSONArray arrMerk = new JSONArray(object1.getString("merk"));
                            merk.add(new RefModel(null,"Semua Merk"));
                            for(int a=0;a<arrMerk.length();a++){
                                JSONObject data = arrMerk.getJSONObject(a);
                                RefModel refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                                merk.add(refModel);
                            }
                            adapter.notifyDataSetChanged();
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
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    RefModel item = merk.get(position);
                    merkKendaraan = item.getId();
                    if(merkKendaraan==null){
                        FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frameLayout, dialogSearchkategori);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    else{
                        ((SearchActivity)getActivity()).setmerkKendaraan(merkKendaraan);
                        FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("merkKendaraan",merkKendaraan);
                        dialogSearchtipe.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frameLayout, dialogSearchtipe);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                }
            });
            return v;
        }
    }
    @SuppressLint("ValidFragment")
    public class DialogSearchTipe extends Fragment {
        private View v;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_dialog_search_tipe, container, false);
            Bundle bundle = this.getArguments();
            String merkKendaraan = bundle.getString("merkKendaraan");
            partsService = new PartsService(getContext());
            dialogSearchkategori = new DialogSearchkategori();

            ListView listView = (ListView) v.findViewById(R.id.list);

            final ArrayAdapter<RefModel> adapter = new ArrayAdapter<RefModel>(getContext(), android.R.layout.simple_list_item_1,tipe);
            listView.setAdapter(adapter);
            partsService.getTipeSearch(merkKendaraan, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject resultData = new JSONObject(result);
                        boolean error = resultData.getBoolean("error");
                        if (!error) {
                            JSONArray arrTipe = resultData.getJSONArray("content");
                            adapter.clear();
                            tipe.add(new RefModel(null,"Semua Tipe"));
                            for(int a=0;a<arrTipe.length();a++){
                                JSONObject data = arrTipe.getJSONObject(a);
                                RefModel refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                                tipe.add(refModel);
                            }
                            adapter.notifyDataSetChanged();

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
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    RefModel item = tipe.get(position);
                    if(item.getId()!=null && !item.getId().equals("null"))
                        ((SearchActivity)getActivity()).settipeKendaraan(item.getId());
                    FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frameLayout, dialogSearchkategori);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            return v;
        }
    }



}

