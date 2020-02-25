package com.preciosclaros;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MapreciosUtils {

    private static MapreciosUtils instance = null;

    private MapreciosUtils() {
    }

    public static MapreciosUtils getInstance() {
        if (instance == null) {
            instance = new MapreciosUtils();
        }
        return instance;
    }

    public static void showAlert(Context context, String mensaje, DialogInterface.OnClickListener okCb,DialogInterface.OnClickListener cancelBtn) {
        new AlertDialog.Builder(context)
                .setNegativeButton("Cancelar",cancelBtn)
                .setCancelable(false)
                .setMessage(mensaje)
                .setPositiveButton("Salir", okCb)
                .setCancelable(false)
                .create()
                .show();
    }

    public static void showAlert(Context context, String mensaje) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
