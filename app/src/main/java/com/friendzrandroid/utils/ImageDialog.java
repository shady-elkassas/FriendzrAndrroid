package com.friendzrandroid.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.friendzrandroid.R;
import com.github.chrisbanes.photoview.PhotoView;


/**
 * Created by mostafa kamal khedr on 16,September,2018
 */
public class ImageDialog {


//    private static ProgressBar progressBar;

    public static void setImageBigger(Activity context, String imagePath) {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_open_image_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);


        /*____________________Inflate Layout___________________________*/

        PhotoView openImage = dialog.findViewById(R.id.iv_open_image);
        RelativeLayout layout = dialog.findViewById(R.id.image_container_image);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
//        Button closeDialog = dialog.findViewById(R.id.btn_close);
//        closeDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//
//            }
//        });
//        progressBar = dialog.findViewById(R.id.loading);


        //set data to components


//        progressBar.setVisibility(View.VISIBLE);
        Glide.with(context).load(imagePath).into(openImage);


        dialog.show();

    }

    public static RequestOptions getRequestOption(Context activity) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        circularProgressDrawable.start();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.image_place_holder_error)
                .priority(Priority.HIGH);
        return options;
    }


}
