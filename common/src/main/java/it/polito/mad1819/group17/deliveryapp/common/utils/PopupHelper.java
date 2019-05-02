package it.polito.mad1819.group17.deliveryapp.common.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import it.polito.mad1819.group17.deliveryapp.common.R;

public class PopupHelper {
    public static void showSnackbar(View v, String msg){
        final Snackbar snackBar = Snackbar
                .make(v, msg, Snackbar.LENGTH_LONG);
        snackBar.setAction(v.getContext().getString(android.R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackBar.dismiss();
                    }
                });
        snackBar.show();
    }

    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg,
                Toast.LENGTH_SHORT).show();
    }
}
