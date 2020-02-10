package com.nklight.ultsub;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;

import com.nklight.ultsub.FloatBuble.FloatBubbleConfig;
import com.nklight.ultsub.FloatBuble.FloatBubbleService;
import com.nklight.ultsub.SubView.SubView;

public class UltSubService extends FloatBubbleService {

    public static final String BUBBLE_SIZE_KEY = "bubbleSizeKey";
    private int bubbleSize = 50;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bubbleSize = intent.getIntExtra(BUBBLE_SIZE_KEY, 50);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected FloatBubbleConfig getConfig() {
        return new FloatBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(getDrawable(R.drawable.ic_sub_round))

                // Set the drawable for the remove bubble
                .removeBubbleIcon(getDrawable(R.drawable.ic_close))

                // Set the size of the bubble in dp
                .bubbleIconDp(bubbleSize)

                // Set the size of the remove bubble in dp
                .removeBubbleIconDp(52)

                // Set the padding of the view from the boundary
                .paddingDp(4)

                // Set the radius of the border of the expandable view
                .borderRadiusDp(4)

                // Does the bubble attract towards the walls
                .physicsEnabled(true)

                // The color of background of the layout
                .expandableColor(Color.TRANSPARENT)

                // The color of the triangular layout
                .triangleColor(R.color.transparent)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.START)

                // The view which is visible in the expanded view
                .expandableView(new SubView(getContext()))

                // Set the alpha value for the remove bubble icon
                .removeBubbleAlpha(0.75f)

                // Building
                .build();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        stopSelf();
//        startService(new Intent(this, UltSubService.class));
    }
}
