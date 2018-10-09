package com.nklight.ultsub.SubView;

import android.content.Context;
import android.view.LayoutInflater;

import com.nklight.ultsub.R;
import com.nklight.ultsub.Utils.CompoundViewHelper;

public class SubView extends CompoundViewHelper.ConstraintLayout {

    private Context mContext;

    public SubView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_full, this, true);
    }
}
