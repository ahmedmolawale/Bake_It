package com.android.example.bakingapp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;

import com.android.example.bakingapp.R;

public class Utility {

    public static void displayMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static Bitmap loadImageAsset(Context context, int position) {

        int[] images = {R.drawable.nutella_pie, R.drawable.brownies, R.drawable.yellow_cake, R.drawable.cheese_cake};
        return BitmapFactory.decodeResource(context.getResources(), images[position]);
    }

    public static int loadImageRes(int position) {
        int[] images = {R.drawable.nutella_pie_1, R.drawable.brownies_1, R.drawable.yellow_cake_1, R.drawable.cheese_cake_1};
        return images[position];
    }
}

