package com.tyagiabhinav.dialogflowchatlibrary.templates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.tyagiabhinav.dialogflowchatlibrary.R;
import com.tyagiabhinav.dialogflowchatlibrary.networkutil.TaskRunner;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class CardMessageTemplate extends MessageLayoutTemplate {

    //private static final String TAG = CardMessageTemplate.class.getSimpleName();

    public CardMessageTemplate(Context context, OnClickCallback callback, int type) {
        super(context, callback, type);
        setOnlyTextResponse(false);
    }

    @Override
    FrameLayout populateRichMessageContainer() {
        FrameLayout richMessageContainer = getRichMessageContainer();
        DetectIntentResponse response = getResponse();
        List<com.google.cloud.dialogflow.v2.Context> contextList = response.getQueryResult().getOutputContextsList();
        LinearLayout cardContainer = getVerticalContainer();

        final LinearLayout cardLayout = getCardLayout(null);
        //LinearLayout btnLayout = getVerticalContainer();
        //btnLayout.setFocusableInTouchMode(true);

        String picture = response.getQueryResult().getFulfillmentText(); //Flask로부터 url 받아오기
        final String imgurl = picture;

                Runnable downloadImage = new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = null;
                        try {
                            InputStream in = new java.net.URL(imgurl).openStream();
                            bmp = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Image download error", e.getMessage());
                            e.printStackTrace();
                        }
                        final Bitmap finalBmp = bmp;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (finalBmp != null) {
                                    ((ImageView) cardLayout.findViewById(R.id.img)).setImageBitmap(finalBmp);
                                } else {
                                    ((ImageView) cardLayout.findViewById(R.id.img)).setImageResource(R.drawable.error_image);
                                }
                            }
                        });
                    }
                };
                new TaskRunner().executeTask(downloadImage);

        cardContainer.addView(cardLayout);
        //cardContainer.addView(btnLayout);
        richMessageContainer.addView(cardContainer);

        return richMessageContainer;
    }


}
