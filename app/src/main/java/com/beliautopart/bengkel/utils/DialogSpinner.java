package com.beliautopart.bengkel.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ListAdapter;
import android.widget.Spinner;

/**
 * Created by brandon on 17/05/16.
 */
public class DialogSpinner extends Spinner {
    public DialogSpinner(Context context) {
        super(context);
    }

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext()).setAdapter((ListAdapter) getAdapter(),
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        setSelection(which);
                        dialog.dismiss();
                    }
                }).create().show();
        return true;
    }
}
