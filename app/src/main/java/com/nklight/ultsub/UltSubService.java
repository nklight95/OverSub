package com.nklight.ultsub;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;

import com.nklight.ultsub.FloatBuble.FloatingBubbleConfig;
import com.nklight.ultsub.FloatBuble.FloatingBubbleService;
import com.nklight.ultsub.SubView.SubView;

public class UltSubService extends FloatingBubbleService {
    @Override
    protected FloatingBubbleConfig getConfig() {
        return new FloatingBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(getDrawable(R.drawable.ic_sub_round))

                // Set the drawable for the remove bubble
                .removeBubbleIcon(getDrawable(R.drawable.triangle_icon))

                // Set the size of the bubble in dp
                .bubbleIconDp(45)

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
                .triangleColor(Color.GREEN)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.END)

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
