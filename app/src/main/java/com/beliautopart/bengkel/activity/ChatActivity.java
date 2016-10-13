package com.beliautopart.bengkel.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.adapter.MessageAdapter;
import com.beliautopart.bengkel.app.AppController;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.MessageModel;
import com.beliautopart.bengkel.webservices.BengkelService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EmptyStackException;

public class ChatActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private ChatActivity context;
    private SessionManager session;
    private BengkelService bengkelService;
    private RecyclerView recyclerViewChat;
    private ArrayList<MessageModel> chatArrayList;
    private MessageAdapter mAdapter;
    private EditText inputPesan;
    private ImageButton btnKirim;
    private String user;
    private String idBengkelUser;
    private String idOrder;
    private Toolbar toolbar;
    private RelativeLayout layoutLoading;
    private ImageButton btnback;
    private TextView txtTitle;
    private String namaBengkel;
    private Handler handler;
    private int jumlahPesan=0;
    private ImageButton btnAtt;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 200;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Query myRef;
    private ChildEventListener getMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        session = new SessionManager(this);
        bengkelService = new BengkelService(this);
        handler = new Handler();

        setContentView(R.layout.activity_chat);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                namaBengkel = "";
            } else {
                namaBengkel = extras.getString("nama");
            }
        } else {
            namaBengkel = (String) savedInstanceState.getSerializable("nama");
        }

        recyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view);

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        btnback.setVisibility(View.VISIBLE);
        txtTitle.setText(namaBengkel);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        inputPesan = (EditText) findViewById(R.id.inputPesan);
        btnKirim = (ImageButton) findViewById(R.id.btnKirim);
        btnAtt = (ImageButton) findViewById(R.id.btnAtt);
        chatArrayList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, chatArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
        recyclerViewChat.setAdapter(mAdapter);


        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPesan.getText().length()<1)
                    Toast.makeText(context,"Pastikan anda telah menulis pesan",Toast.LENGTH_SHORT).show();
                else{
                    long millis = System.currentTimeMillis();
                    byte[] data = new byte[0];
                    try {
                        data = inputPesan.getText().toString().getBytes("UTF-8");
                        final MessageModel model = new MessageModel(session.getUserId(),""+Base64.encodeToString(data, Base64.DEFAULT),session.getUserNama(),""+millis,false,false);
                        model.setSent(false);
                        model.setSending(true);
                        chatArrayList.add(model);
                        if (mAdapter.getItemCount() > jumlahPesan) {
                            recyclerViewChat.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1 );
                        }
                        mAdapter.notifyDataSetChanged();
                        bengkelService.setPesan(session.getBengkelId(),session.getUserId(),session.getBengkelUserId(), inputPesan.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    if(!object.getBoolean("error")){
                                    }
                                    else{
                                        byte[] data = Base64.decode(model.getMsg(), Base64.DEFAULT);
                                        String text = new String(data, "UTF-8");
                                        chatArrayList.remove(model);

                                        inputPesan.setText(text);
                                        Toast.makeText(context,"Pesan gagal",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                        inputPesan.setText("");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT < 23 ||
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    selectImage();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                    }
                }
            }
        });

        recyclerViewChat.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewChat, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MessageModel chat = chatArrayList.get(position);
                if(chat.isFile()){
                    byte[] data = Base64.decode(chat.getMsg(), Base64.DEFAULT);
                    try {
                        String text = new String(data, "UTF-8");
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        startActivity(intent);
                        intent.putExtra("image","http://beliautopart.com/~uploads/"+text.replaceAll(" ", "%20"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        chatArrayList.clear();
        mDatabase = FirebaseDatabase.getInstance();
        Log.d("sess ",session.getBengkelId());
        myRef = mDatabase.getReference("jobs").orderByChild("id").equalTo(session.getBengkelId());
        getMessage = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildAdded:" + dataSnapshot.child("messages").getValue());
                chatArrayList.clear();

                for (DataSnapshot messageSnapshot: dataSnapshot.child("messages").getChildren()) {
                    Log.d("userId",""+session.getUserId());
                    String userId = (String) messageSnapshot.child("id").getValue();

                    Log.d("",""+userId);
                    String msg = (String) messageSnapshot.child("msg").getValue();
                    long waktu = (long) messageSnapshot.child("waktu").getValue();
                    String nama = (String) messageSnapshot.child("id").getValue();
                    boolean file = (boolean) messageSnapshot.child("is_file").getValue();
                    boolean read = (boolean) messageSnapshot.child("is_read").getValue();
                    boolean delivered = (boolean) messageSnapshot.child("is_sent").getValue();
                    MessageModel model = new MessageModel(userId,msg,nama,""+waktu,file,read);
                    model.setSent(delivered);
                    model.setSending(false);
                    if(!userId.contentEquals(session.getUserId())){
                        mDatabase.getReference("jobs").child(dataSnapshot.getKey()).child("messages").child(messageSnapshot.getKey()).child("is_read").setValue(true);
                    }
                    chatArrayList.add(model);
                }

                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > jumlahPesan) {
                    recyclerViewChat.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1 );
                }

                mAdapter.notifyDataSetChanged();


                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildChanged:" + dataSnapshot.child("messages").getValue());
                int a=0;
                for (DataSnapshot messageSnapshot: dataSnapshot.child("messages").getChildren()) {
                    String userId = (String) messageSnapshot.child("id").getValue();
                    String msg = (String) messageSnapshot.child("msg").getValue();
                    long waktu = (long) messageSnapshot.child("waktu").getValue();
                    String nama = (String) messageSnapshot.child("id").getValue();
                    boolean file = (boolean) messageSnapshot.child("is_file").getValue();
                    boolean read = (boolean) messageSnapshot.child("is_read").getValue();
                    boolean delivered = (boolean) messageSnapshot.child("is_sent").getValue();
                    MessageModel model = new MessageModel(userId,msg,nama,""+waktu,file,read);
                    model.setSent(delivered);
                    model.setSending(false);
                    if(!userId.contentEquals(session.getUserId())){
                        mDatabase.getReference("jobs").child(dataSnapshot.getKey()).child("messages").child(messageSnapshot.getKey()).child("is_read").setValue(true);
                    }
                    if(a<chatArrayList.size()){
                        chatArrayList.remove(a);
                        chatArrayList.add(a,model);
                        a=a+1;
                    }
                    else{
                        chatArrayList.add(model);
                        a=a+1;
                    }

                }

                if (mAdapter.getItemCount() > jumlahPesan) {
                    recyclerViewChat.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1 );
                }
                mAdapter.notifyDataSetChanged();


                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("tag", "onChildRemoved:" + dataSnapshot.child("messages").getValue());


                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildMoved:" + dataSnapshot.child("messages").getValue());


                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("tag", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        myRef.addChildEventListener(getMessage);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = null;
                        session = new SessionManager(this);
                        String filename = selectedImage.getLastPathSegment()+".jpg";
                        imageStream = getContentResolver().openInputStream(selectedImage);
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
                        long millis = System.currentTimeMillis();
                        byte[] data = new byte[0];
                        String text ="placeholder.png";
                        try {
                            data = text.getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        final MessageModel model = new MessageModel(session.getUserId(),""+Base64.encodeToString(data, Base64.DEFAULT),session.getUserNama(),""+millis,true,false);
                        model.setSent(false);
                        model.setSending(true);
                        chatArrayList.add(model);
                        if (mAdapter.getItemCount() > jumlahPesan) {
                            recyclerViewChat.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1 );
                        }
                        mAdapter.notifyDataSetChanged();
                        bengkelService.setImagePesan(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(), filename, file, new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                Toast.makeText(context,"Pesan berhasil terkirim",Toast.LENGTH_SHORT).show();
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        String filename = "" +System.currentTimeMillis();
                        Bitmap fileBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        fileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] fileBitmapArray = baos.toByteArray();
                        String file = Base64.encodeToString(fileBitmapArray, Base64.DEFAULT);
                        long millis = System.currentTimeMillis();
                        byte[] data = new byte[0];
                        String text ="placeholder.png";
                        try {
                            data = text.getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        final MessageModel model = new MessageModel(session.getUserId(),""+Base64.encodeToString(data, Base64.DEFAULT),session.getUserNama(),""+millis,true,false);
                        model.setSent(false);
                        model.setSending(true);
                        chatArrayList.add(model);
                        if (mAdapter.getItemCount() > jumlahPesan) {
                            recyclerViewChat.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1 );
                        }
                        mAdapter.notifyDataSetChanged();
                        bengkelService.setImagePesan(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(), filename, file, new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                Toast.makeText(context,"Pesan berhasil terkirim",Toast.LENGTH_SHORT).show();
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });

                    } catch (EmptyStackException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else if (options[item].equals("Choose from Gallery"))
                {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Runnable getChat = new Runnable() {
        @Override
        public void run() {
            bengkelService.getPesan(session.getBengkelId(), session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if(!object.getBoolean("error")){
                            JSONArray dataArray = new JSONArray(object.getString("content"));
                            for(int a=jumlahPesan;a<dataArray.length();a++){
                                JSONObject data = dataArray.getJSONObject(a);
                                boolean file = false;
                                if(data.getString("is_file").equals("1"))
                                    file=true;

                                //MessageModel model = new MessageModel(data.getString("src_uid"),data.getString("msg"),data.getString("nama"),data.getString("waktu"),file);
                                //chatArrayList.add(model);
                            }
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > jumlahPesan) {
                                recyclerViewChat.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1 );
                            }
                            jumlahPesan = mAdapter.getItemCount();
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
            handler.postDelayed(getChat,1000);

        }

    };
    public void startGetChat(){
        handler.post(getChat);
    }
    public void stopGetChat(){
        handler.removeCallbacks(getChat);
    }


    @Override
    public void onResume(){
        super.onResume();
        //startGetChat();
    }
    @Override
    public void onPause(){
        super.onPause();
        //stopGetChat();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        myRef.removeEventListener(getMessage);
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        myRef.removeEventListener(getMessage);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChatActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ChatActivity.ClickListener clickListener) {
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

}
