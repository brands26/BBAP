package com.beliautopart.bengkel.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.bengkel.R;
import com.beliautopart.bengkel.activity.BengkelActivity;
import com.beliautopart.bengkel.activity.CartActivity;
import com.beliautopart.bengkel.helper.SendDataHelper;
import com.beliautopart.bengkel.helper.SessionManager;
import com.beliautopart.bengkel.model.UserModel;
import com.beliautopart.bengkel.webservices.UserService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by brandon on 12/05/16.
 */
public class LoginFragment extends Fragment {
    private EditText inputEmail;
    private EditText inputPassword;
    private RelativeLayout btnLogin;
    private UserService userService;
    private SessionManager session;
    private RelativeLayout txtRegister;
    private Dialog dialogStatus;
    private EditText inputEmailDialog;
    private RelativeLayout lbtnForgot;
    private Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.login_fragment, container, false);
        inputEmail = (EditText) v.findViewById(R.id.inputEmail);
        inputPassword = (EditText) v.findViewById(R.id.inputKomplain);
        btnLogin = (RelativeLayout) v.findViewById(R.id.lbtnLogin);
        userService = new UserService(getContext());
        session = new SessionManager(getContext());
        txtRegister = (RelativeLayout) v.findViewById(R.id.lbtnRegister);
        lbtnForgot = (RelativeLayout) v.findViewById(R.id.lbtnForgot);
        context = (Activity)getContext();
        if(session.isLoggedIn()){
            getActivity().finish();
            Toast.makeText(getContext(),"Anda Sudah Login",Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputEmail.getText().toString().equals(null))
                    Toast.makeText(getContext(), "Email belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputPassword.getText().toString().equals(null))
                    Toast.makeText(getContext(), "Password belum diisi", Toast.LENGTH_SHORT).show();
                else
                    onLogin();
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonClick();
            }
        });
        lbtnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogShow();
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return v;
    }

    private void onLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        UserModel user = new UserModel(email, password);
        user.setFID(session.getFID());
        userService.LoginUser(user, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject dataUser = new JSONObject(obj.getString("content"));
                        session.setLogin(true);
                        session.setToken(dataUser.getString("uid_firebase"));
                        session.setUserId(dataUser.getString("id"));
                        session.setUserNama(dataUser.getString("nama"));
                        if(session.getOrderAktif()){
                            Intent i = new Intent(context, CartActivity.class);
                            startActivity(i);
                            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            getActivity().finish();
                        }
                        else if(session.getbengkelAktif()){
                            Intent i = new Intent(context, BengkelActivity.class);
                            startActivity(i);
                            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            getActivity().finish();
                        }
                        else{
                            getActivity().finish();
                        }
                    } else {
                        Toast.makeText(getContext(), obj.getString("content"), Toast.LENGTH_LONG).show();
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
    }

    public void onRegisterButtonClick() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameAuth, registerFragment);
        fragmentTransaction.commit();
    }

    public void DialogShow(){
        dialogStatus = new Dialog(getContext());
        dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStatus.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogStatus.setContentView(R.layout.dialog_lupa_pass);
        dialogStatus.setCancelable(true);
        inputEmailDialog = (EditText) dialogStatus.findViewById(R.id.inputEmail);
        Button btnOK = (Button) dialogStatus.findViewById(R.id.btnOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputEmailDialog.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Email belum diisi", Toast.LENGTH_SHORT).show();
                else
                    userService.ForgotPasswordUser(inputEmailDialog.getText().toString(), new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            try {
                                JSONObject obj = new JSONObject(result);

                                // check for error
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getContext(), obj.getString("content"), Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getContext(), obj.getString("content"), Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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
        dialogStatus.show();
    }



}
