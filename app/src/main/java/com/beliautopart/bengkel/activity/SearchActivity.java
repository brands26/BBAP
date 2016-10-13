package com.beliautopart.bengkel.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.adapter.CartAdapter;
import com.beliautopart.bengkel.adapter.EndlessRecyclerViewScrollListener;
import com.beliautopart.bengkel.adapter.ItemListAdapter;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.DIalogSearch;
import com.beliautopart.bengkel.helper.Logic;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.ItemProduk;
import com.beliautopart.bengkel.model.RefModel;
import com.beliautopart.bengkel.model.SearchOptionModel;
import com.beliautopart.bengkel.utils.GPSTracker;
import com.beliautopart.bengkel.webservices.PartsService;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircularProgressView progressView;
    private PartsService partsService;
    private RelativeLayout loadingView;
    private List<ItemProduk> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemListAdapter mAdapter;
    private String kat;
    private ImageButton cartButton;
    private ImageButton searchButton;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private SessionManager session;
    private List<ItemProduk> cartList;
    private TextView countTextview;
    private ImageButton btnback;
    private TextView txtTitle;
    private Handler handler;

    private List<RefModel> jenis = new ArrayList<>();
    private List<RefModel> kategori= new ArrayList<>();
    private List<RefModel> merk= new ArrayList<>();
    private List<RefModel> tipe= new ArrayList<>();
    private RelativeLayout btnSearch;
    private String jenisKendaraan="0";
    private String kategoriKendaraan="0";
    private String merkKendaraan="0";
    private String tipeKendaraan="0";
    private CardView txtTidakAda;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private Animation rotationA;
    private Animation rotation;
    private Animation rotationC;
    private GPSTracker gps;
    private DIalogSearch dialog;
    private String[] tahunArray;
    private Logic logic;
    private FragmentManager fm;
    private Dialog dialogDetailItem;
    private Dialog dialogError;
    private TextView dialogErrorMessage;
    private Button dialogErrorYes;
    private SearchOptionModel searchOption;
    private String kategoriSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        partsService = new PartsService(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                kat = "1";
            } else {
                kat = extras.getString("kat");
            }
        } else {
            kat = (String) savedInstanceState.getString("page");
        }

        /*


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_search_item);
        */


        dialog = new DIalogSearch();
        fm = getSupportFragmentManager();


        dialogDetailItem = new Dialog(this);
        dialogDetailItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDetailItem.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogDetailItem.setContentView(R.layout.dialog_item_detail);


        dialogError = new Dialog(this);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);
        dialogErrorMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        dialogErrorYes = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        dialogErrorMessage.setText("Apakah anda besedia barang pesanan tsb kami carikan dgn waktu yg lebih lama?");
        dialogErrorYes.setText("ya");

        session = new SessionManager(this);
        gps = new GPSTracker(this);
        handler = new Handler();
        logic = new Logic();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cartButton = (ImageButton) findViewById(R.id.btnCart);
        countTextview = (TextView) findViewById(R.id.badge_textView);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        btnSearch = (RelativeLayout) findViewById(R.id.lbtnCari);
        txtTidakAda = (CardView) findViewById(R.id.card_view_relativeLayout11);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Parts");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);


        loadingView = (RelativeLayout) findViewById(R.id.loadingLayout);

        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnback.setVisibility(View.VISIBLE);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartButton.setVisibility(View.GONE);
        /*
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps.canGetLocation()) {
                    if(session.getCart()==null){
                        showCartAlert();
                    }
                    else{
                        Intent i = new Intent(v.getContext(), CartActivity.class);
                        i.putExtra("kat", kat);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                    }

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Peringatan GPS");
                    alertDialog.setMessage("GPS tidak aktif. Aktifkan sekarang?");
                    alertDialog.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        */
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopOnLoadingAnimation();
                onDialogSearch();
                /*
                partsService.getSearchMenu(kat,0, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if(!object.getBoolean("error")){
                                JSONObject object1 = new JSONObject(object.getString("content"));
                                JSONArray arrJenis = new JSONArray(object1.getString("jenis"));
                                JSONArray arrkategori = new JSONArray(object1.getString("kategori"));
                                JSONArray arrMerk = new JSONArray(object1.getString("merk"));
                                RefModel refModel =  new RefModel();
                                jenis.clear();
                                kategori.clear();
                                merk.clear();
                                refModel.setNama("Semua Jenis");
                                jenis.add(refModel);
                                kategori.add(refModel);
                                merk.add(refModel);
                                for(int a=0;a<arrJenis.length();a++){
                                    JSONObject data = arrJenis.getJSONObject(a);
                                    refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                                    jenis.add(refModel);
                                }
                                for(int a=0;a<arrkategori.length();a++){
                                    JSONObject data = arrkategori.getJSONObject(a);
                                    refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                                    kategori.add(refModel);
                                }
                                for(int a=0;a<arrMerk.length();a++){
                                    JSONObject data = arrMerk.getJSONObject(a);
                                    refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                                    merk.add(refModel);
                                }
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
            */
            }
        });
        mAdapter = new ItemListAdapter(itemList);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ItemProduk item = itemList.get(position);
                onDialogDetailItem(item);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("page s",""+page);
                customLoadMoreDataFromApi(page);
            }
        });
        int Total = session.getCountOrder();
        if(Total>0){
            startGetTime();
            countTextview.setVisibility(View.VISIBLE);
            countTextview.setText(""+Total);
        }
        else{
            stopGetTime();
            countTextview.setVisibility(View.GONE);
        }

        startOnLoadingAnimation();
        btnSearch.performClick();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("page",kat);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        kat = savedInstanceState.getString("page");
    }


    public void customLoadMoreDataFromApi(final int page) {
        searchOption.setPage(""+page);
        partsService.SearchParts(searchOption, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                JSONObject resultData = null;
                try {
                    resultData = new JSONObject(result);
                    boolean error = resultData.getBoolean("error");
                    if (!error) {
                        JSONArray dataArray = resultData.getJSONArray("content");
                        Log.d("content",result);
                        int sizeDataArray = dataArray.length();
                        for (int a = 0; a < sizeDataArray; a++) {
                            JSONObject dataItem = dataArray.getJSONObject(a);
                            int idItem = dataItem.getInt("id_item");
                            String namaItem = dataItem.getString("nama_item");
                            int sts = 0;
                            if(!dataItem.getString("qty").equals("null"))
                                sts = dataItem.getInt("qty");
                            int harga = dataItem.getInt("harga");
                            String kompatibel = dataItem.getString("kompatibel");
                            String made = dataItem.getString("made");
                            String kode = dataItem.getString("kode");
                            String foto = dataItem.getString("foto");
                            String kategori = dataItem.getString("kategori");
                            Log.d("made",made);
                            ItemProduk itemProduk = new ItemProduk(idItem,kategori, namaItem,made, sts, harga, kompatibel, kode, foto);
                            itemList.add(itemProduk);
                        }


                    }
                    else{
                        if(page==0)
                            txtTidakAda.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                stopOnLoadingAnimation();
                return result;
            }

            @Override
            public String onError(VolleyError error) {
                return null;
            }
        });
    }

    public void startOnLoadingAnimation(){
        loadingView.setVisibility(View.VISIBLE);
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
    }
    public void stopOnLoadingAnimation(){
        loadingView.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }

    @Override
    protected void onResume(){
        super.onResume();
        session = new SessionManager(this);
        partsService = new PartsService(this);

        /*
        onSearch( new SearchOptionModel());
        int Total = session.getCountOrder();
        if(Total>0){
            countTextview.setVisibility(View.VISIBLE);
            countTextview.setText(""+Total);
        }
        else{
            countTextview.setVisibility(View.GONE);
        }
        */
    }
    @Override
    protected void onPause(){
        super.onPause();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        stopGetTime();
        AppController.getInstance().cancelPendingRequests("volley");
    }

    public void onSearch(SearchOptionModel searchOption) {
        startOnLoadingAnimation();
        this.searchOption = searchOption;
        if(searchOption.getTipe()==null || searchOption.getTipe().equals("0"))
            searchOption.setTipe(null);
        if(searchOption.getMerk()==null || searchOption.getMerk().equals("0"))
            searchOption.setMerk(null);
        partsService.SearchParts(searchOption, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                JSONObject resultData = null;
                itemList.clear();
                try {
                    resultData = new JSONObject(result);
                    boolean error = resultData.getBoolean("error");
                    if (!error) {
                        txtTidakAda.setVisibility(View.GONE);
                        JSONArray dataArray = resultData.getJSONArray("content");
                        Log.d("content",result);
                        int sizeDataArray = dataArray.length();
                        for (int a = 0; a < sizeDataArray; a++) {
                            JSONObject dataItem = dataArray.getJSONObject(a);
                            int idItem = dataItem.getInt("id_item");
                            String namaItem = dataItem.getString("nama_item");
                            int sts = 0;
                            if(!dataItem.getString("qty").equals("null"))
                                sts = dataItem.getInt("qty");
                            int harga = dataItem.getInt("harga");
                            String kompatibel = dataItem.getString("kompatibel");
                            String made = dataItem.getString("made");
                            String kode = dataItem.getString("kode");
                            String foto = dataItem.getString("foto");
                            String kategori = dataItem.getString("kategori");
                            ItemProduk itemProduk = new ItemProduk(idItem,kategori, namaItem,made, sts, harga, kompatibel, kode, foto);
                            itemList.add(itemProduk);
                        }


                    }
                    else{
                        txtTidakAda.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                stopOnLoadingAnimation();
                return result;
            }

            @Override
            public String onError(VolleyError error) {
                return null;
            }
        });
    }

    private void onDialogCart(){
        CartAdapter cartAdapter = null;
        final List<ItemProduk> cartProdukList;
        if(session.getCart()!=null)
            cartProdukList = session.getCart();
        else
            cartProdukList= new ArrayList<>();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_cart);
        dialog.setCancelable(true);
        RecyclerView recyclerViewDialog = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        final RelativeLayout cartStatusLayout = (RelativeLayout) dialog.findViewById(R.id.cartStatusLayout);
        Button btnKosongkanCart = (Button) dialog.findViewById(R.id.btnCari);
        Button btnCheckOut = (Button) dialog.findViewById(R.id.btnCheckOut);
        if(cartProdukList.size()>0){
            cartStatusLayout.setVisibility(View.GONE);
            cartAdapter = new CartAdapter(cartProdukList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerViewDialog.setLayoutManager(mLayoutManager);
            recyclerViewDialog.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDialog.setAdapter(cartAdapter);
        }
        final CartAdapter finalCartAdapter = cartAdapter;
        btnKosongkanCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalCartAdapter!=null){
                    session.emptyCart();
                    cartProdukList.clear();
                    cartStatusLayout.setVisibility(View.VISIBLE);
                    finalCartAdapter.notifyDataSetChanged();
                    countTextview.setText(""+session.getCountOrder());
                }
                else{
                    Toast.makeText(v.getContext(),"Cart sudah kosong",Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    private void onDialogSearch(){
        Fragment dialogShow = getFragmentManager().findFragmentByTag("Search item");
        if(dialogShow==null && !dialogDetailItem.isShowing())
            dialog.show(fm,"Search item");


        /*
        DialogSearchkategori f1 = new DialogSearchkategori();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LayoutInflater inflater = getLayoutInflater();
        FrameLayout f1 = (FrameLayout) dialog.findViewById(R.id.Frame);
        f1.addView(inflater.inflate(R.layout.dialog_search_item, f1, false));
        Spinner spJenis = (Spinner) dialog.findViewById(R.id.spJenis);
        Spinner spKategori = (Spinner) dialog.findViewById(R.id.spKategori);
        Spinner spMerk = (Spinner) dialog.findViewById(R.id.spMerk);
        Spinner spTahun = (Spinner) dialog.findViewById(R.id.spTahun);
        final Spinner spTipe = (Spinner) dialog.findViewById(R.id.spTipe);
        tahunArray = getResources().getStringArray(R.array.tahun);
        final ArrayAdapter spinnerArrayAdapter5 = new ArrayAdapter(this,
                R.layout.spinner_item, tahunArray);
        spinnerArrayAdapter5.setDropDownViewResource( R.layout.spinner_item );
        spTahun.setAdapter(spinnerArrayAdapter5);
        ArrayAdapter<RefModel> spinnerArrayAdapter = new ArrayAdapter<RefModel>(this,
                R.layout.spinner_item, jenis);
        spinnerArrayAdapter.setDropDownViewResource( R.layout.spinner_item );
        spJenis.setAdapter(spinnerArrayAdapter);
        ArrayAdapter<RefModel> spinnerArrayAdapter1 = new ArrayAdapter<RefModel>(this,
                R.layout.spinner_item, kategori);
        spinnerArrayAdapter1.setDropDownViewResource( R.layout.spinner_item );
        spKategori.setAdapter(spinnerArrayAdapter1);
        ArrayAdapter<RefModel> spinnerArrayAdapter2 = new ArrayAdapter<RefModel>(this,
                R.layout.spinner_item, merk);
        spinnerArrayAdapter2.setDropDownViewResource( R.layout.spinner_item );
        spMerk.setAdapter(spinnerArrayAdapter2);
        spJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RefModel model = jenis.get(position);
                jenisKendaraan = model.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RefModel model = kategori.get(position);
                kategoriKendaraan = model.getId();
                Log.d("jenisKendaraan",""+ model.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spMerk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                RefModel model = merk.get(position);
                if(model.getId()!=null){
                    merkKendaraan = model.getId();
                }
                else
                    merkKendaraan = "0";
                if(!merkKendaraan.equals("0")){
                    partsService.getTipeSearch(merkKendaraan, new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            try {
                                JSONObject resultData = new JSONObject(result);
                                boolean error = resultData.getBoolean("error");
                                tipe.clear();
                                RefModel refModel = new RefModel();
                                refModel.setNama("Semua Tipe");
                                tipe.add(refModel);
                                if (!error) {
                                    JSONArray dataArray = resultData.getJSONArray("content");
                                    int sizeDataArray = dataArray.length();
                                    for(int a=0;a<sizeDataArray;a++){
                                        JSONObject data = dataArray.getJSONObject(a);
                                        refModel = new RefModel(data.getString("id"), data.getString("nama"));
                                        tipe.add(refModel);
                                    }
                                    ArrayAdapter<RefModel> spinnerArrayAdapter3 = new ArrayAdapter<RefModel>(getApplicationContext(),
                                            R.layout.spinner_item, tipe);
                                    spinnerArrayAdapter3.setDropDownViewResource( R.layout.spinner_item );
                                    spinnerArrayAdapter3.notifyDataSetChanged();
                                    spTipe.setAdapter(spinnerArrayAdapter3);

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
                else{
                    tipe.clear();
                    RefModel refModel = new RefModel();
                    refModel.setNama("Semua Tipe");
                    tipe.add(refModel);
                    ArrayAdapter<RefModel> spinnerArrayAdapter3 = new ArrayAdapter<RefModel>(getApplicationContext(),
                            R.layout.spinner_item, tipe);
                    spinnerArrayAdapter3.setDropDownViewResource( R.layout.spinner_item );
                    spinnerArrayAdapter3.notifyDataSetChanged();
                    spTipe.setAdapter(spinnerArrayAdapter3);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spTipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    RefModel model = tipe.get(position);
                    tipeKendaraan = model.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button btnSearch = (Button) dialog.findViewById(R.id.btnSimpan);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SearchOptionModel searchOption = new SearchOptionModel();

                searchOption.setJenis(jenisKendaraan);
                searchOption.setKatItem(kategoriKendaraan);
                if(merkKendaraan.equals("0"))
                    merkKendaraan="";
                searchOption.setMerk(merkKendaraan);
                searchOption.setKat(kat);
                if(tipeKendaraan.equals("0"))
                    tipeKendaraan="";
                searchOption.setTipe(tipeKendaraan);
                startOnLoadingAnimation();
                partsService.SearchParts(searchOption, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        JSONObject resultData = null;
                        itemList.clear();
                        try {
                            resultData = new JSONObject(result);
                            boolean error = resultData.getBoolean("error");
                            if (!error) {
                                txtTidakAda.setVisibility(View.GONE);
                                JSONArray dataArray = resultData.getJSONArray("content");
                                int sizeDataArray = dataArray.length();
                                for (int a = 0; a < sizeDataArray; a++) {
                                    JSONObject dataItem = dataArray.getJSONObject(a);
                                    int idItem = dataItem.getInt("id_item");
                                    String namaItem = dataItem.getString("nama_item");
                                    int sts = dataItem.getInt("id_item");
                                    int harga = dataItem.getInt("harga");
                                    String kompatibel = dataItem.getString("kompatibel");
                                    String kode = dataItem.getString("kode");
                                    String foto = dataItem.getString("foto");
                                    String kategori = dataItem.getString("kategori");
                                    ItemProduk itemProduk = new ItemProduk(idItem,kategori, namaItem, sts, harga, kompatibel, kode, foto);
                                    itemList.add(itemProduk);
                                }


                            }
                            else{
                                txtTidakAda.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        stopOnLoadingAnimation();
                        return result;
                    }

                    @Override
                    public String onError(VolleyError error) {
                        return null;
                    }
                });
            }
        });




        if(!dialog.isShowing())
            dialog.show();
        */
    }

    private void onDialogDetailItem(final ItemProduk item){
        NetworkImageView imgProduk = (NetworkImageView) dialogDetailItem.findViewById(R.id.imgProduk);
        TextView namaProduk = (TextView) dialogDetailItem.findViewById(R.id.txtNamaProduk);
        TextView kodeProduk = (TextView) dialogDetailItem.findViewById(R.id.txtKodeProduk);
        TextView kompatibel = (TextView) dialogDetailItem.findViewById(R.id.txtKompatibel);
        TextView hargaProduk = (TextView) dialogDetailItem.findViewById(R.id.txtHargaProduk);
        TextView txtStok = (TextView) dialogDetailItem.findViewById(R.id.textView115);
        Button btnBeli = (Button) dialogDetailItem.findViewById(R.id.btnCari);
        Button btnBatal = (Button) dialogDetailItem.findViewById(R.id.btnSimpan);
        namaProduk.setText(item.getNamaItem());
        kodeProduk.setText(item.getKode());
        kompatibel.setText(item.getKompatibel().replace(";", ", "));
        //hargaProduk.setText("Rp"+logic.thousand(""+item.getHarga()));
        hargaProduk.setText("Made in:"+item.getMade());

        imgProduk.setImageUrl("http://beliautopart.com/_produk/thumbnail/"+item.getFoto().replaceAll(" ","%20"),imageLoader);
        if(item.getSts()>0){
            txtStok.setText("Ready Stock");
            txtStok.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
        }
        else{
            txtStok.setText("Out Of Stock");
            txtStok.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        }
        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startGetTime();
                gps = new GPSTracker(getApplicationContext());
                if (gps.canGetLocation()) {
                    if(item.getSts()==0){
                        showOOSAlert(item);
                    }
                    else{
                        dialogDetailItem.dismiss();
                        session.setOrderAktif(true);
                        item.setJumlah(1);
                        session.setCart(item);
                        Intent i = new Intent(v.getContext(), CartActivity.class);
                        i.putExtra("kat", kat);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Peringatan GPS");
                    alertDialog.setMessage("GPS tidak aktif. Aktifkan sekarang?");
                    alertDialog.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent,1);
                        }
                    });
                    alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetailItem.dismiss();
            }
        });
        dialogDetailItem.show();
    }

    public String getkategori() {
        return kategoriSearch;
    }

    public String getmerkKendaraan() {
        return merkKendaraan;
    }

    public String gettipeKendaraan() {
        return tipeKendaraan;
    }
    public void setkat(String kat) {
        this.kat= kat;
    }
    public void setkategori(String kategoriSearch) {
        this.kategoriSearch= kategoriSearch;
    }

    public void setmerkKendaraan(String merkKendaraan) {
        this.merkKendaraan= merkKendaraan;
    }

    public void settipeKendaraan(String tipeKendaraan) {
        this.tipeKendaraan= tipeKendaraan;
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private SearchActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SearchActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    private String thousand(String number){
        StringBuilder strB = new StringBuilder();
        strB.append(number);
        int Three = 0;

        for(int i=number.length();i>0;i--){
            Three++;
            if(Three == 3){
                strB.insert(i-1, ".");
                Three = 0;
            }
        }
        return strB.toString();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void showCartAlert(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_alert_order_aktif);
        dialog.setCancelable(true);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        TextView txtText = (TextView) dialog.findViewById(R.id.textView27);
        txtText.setText("Shopping Cart masih kosong");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private int countColor = 0;
    boolean isRunning = false;
    public Runnable getTimer = new Runnable() {
        @Override
        public void run() {
            if(countColor == 1){
                countTextview.setVisibility(View.GONE);
                countTextview.setBackgroundResource(R.drawable.bagde_circle_red);
                countTextview.setTextColor(getResources().getColor(R.color.yellow));
                countColor = 0;
            }
            else {
                countTextview.setVisibility(View.GONE);
                countTextview.setBackgroundResource(R.drawable.bagde_circle_yellow);
                countTextview.setTextColor(getResources().getColor(R.color.colorPrimary));
                countColor = 1;
            }
            handler.postDelayed(getTimer,500);

        }

    };
    public void startGetTime(){
        if(!isRunning){
            handler.post(getTimer);
            isRunning=true;
        }

    }

    public void stopGetTime(){
        handler.removeCallbacks(getTimer);
        isRunning=false;
    }

    public String getKat(){
        return kat;
    }

    public void showOOSAlert(final ItemProduk item){
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);

        if(session.getbengkelAktif() || !session.getBengkelIdOrder().equals("")){
            dialogErrorMessage.setText("Maaf barang yang anda inginkan sedang tidak tersedia.");
            btnCobaLagi.setVisibility(View.GONE);
        }
        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setOrderAktif(true);
                item.setJumlah(1);
                session.setCart(item);
                Intent i = new Intent(v.getContext(), CartActivity.class);
                i.putExtra("kat", kat);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
        if (dialogError != null && !dialogError.isShowing())
            dialogError.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    if(session.getCart() != null && session.isLoggedIn()){
                        Intent i = new Intent(this, CartActivity.class);
                        i.putExtra("kat", kat);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    break;
            }
        }
    }

}
