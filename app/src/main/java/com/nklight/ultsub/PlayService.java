package com.nklight.ultsub;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.nklight.ultsub.Utils.LogUtils;

public class PlayService extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("service", "bind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("service", "create");
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_full, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 0;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

//        Button btnClose = mChatHeadView.findViewById(R.id.button);
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopSelf();
//            }
//        });
    }

    private void initView() {

    }

    private void handleTouch() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("service", "destroy");
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }

}
