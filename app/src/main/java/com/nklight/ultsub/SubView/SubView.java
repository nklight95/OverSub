package com.nklight.ultsub.SubView;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nklight.ultsub.R;
import com.nklight.ultsub.Subtitle.Subtitle;
import com.nklight.ultsub.Subtitle.Timestamp;
import com.nklight.ultsub.Utils.CompoundViewHelper;
import com.nklight.ultsub.Utils.Utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubView extends CompoundViewHelper.ConstraintLayout implements SubHandler.SubHandlerListener {

    private Context mContext;
    private String timeLabel;
    private TextView tvSub;
    private ToggleButton btnStart;
    private SubHandler mHandler;
    private InputStream inputStream;
    private State state;
    private TextView tvTime;

    public SubView(Context context) {
        super(context);
        init(context);
    }

    public SubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        timeLabel = "00:00:00,000";
        LayoutInflater.from(context).inflate(R.layout.layout_full, this, true);
        tvSub = findViewById(R.id.tvSub);
        btnStart = findViewById(R.id.btnStart);
        tvTime = findViewById(R.id.textView2);
        tvTime.setText(timeLabel);
        mHandler = new SubHandler(Looper.getMainLooper(), this, context, btnStart, timeLabel);
        inputStream = getResources().openRawResource(R.raw.one);
        state = new State(0L, new ArrayList<Subtitle>(), new ArrayList<Timestamp>(), btnStart, timeLabel);
        mHandler.setSubtitle(inputStream, state);
    }


    @Override
    public void onTextChange(final String text,final String time) {
        this.post(new Runnable() {
            @Override
            public void run() {
                tvSub.setText(text);
                tvTime.setText(time);

            }
        });
    }

    @Override
    public void onError(final String error) {
        this.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onTextChange(final List<String> texts,final String time) {
        this.post(new Runnable() {
            @Override
            public void run() {
                tvSub.setText(Utils.joinToString(texts));
                tvTime.setText(time);
            }
        });

    }
}
