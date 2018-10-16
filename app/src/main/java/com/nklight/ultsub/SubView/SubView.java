package com.nklight.ultsub.SubView;

import android.content.Context;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nklight.ultsub.R;
import com.nklight.ultsub.Subtitle.Subtitle;
import com.nklight.ultsub.Subtitle.Timestamp;
import com.nklight.ultsub.Utils.CompoundViewHelper;
import com.nklight.ultsub.Utils.DownloadCallback;
import com.nklight.ultsub.Utils.DownloadFile;
import com.nklight.ultsub.Utils.LogUtils;
import com.nklight.ultsub.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubView extends CompoundViewHelper.ConstraintLayout implements SubHandler.SubHandlerListener {

    private Context mContext;
    private String timeLabel;
    private HtmlTextView tvSub;
    private ToggleButton btnStart;
    private SubHandler mHandler;
    private InputStream inputStream;
    private State state;
    private TextView tvTime;
    private Button btnBackWard;
    private Button btnForward;
    private ConstraintLayout parentLayout;
    private ConstraintLayout containerButton;

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
        LayoutInflater.from(context).inflate(R.layout.layout_full, this, true);
        findView();

        timeLabel = "00:00:00,000";
        tvTime.setText(timeLabel);
        mHandler = new SubHandler(Looper.getMainLooper(), this, context, btnStart, timeLabel);
        inputStream = getResources().openRawResource(R.raw.one);
        state = new State(0L, new ArrayList<Subtitle>(), new ArrayList<Timestamp>(), btnStart, timeLabel);
        mHandler.setSubtitle(inputStream, state);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    private void findView() {
        btnBackWard = findViewById(R.id.btnBackward);
        btnForward = findViewById(R.id.btnForward);
        tvSub = findViewById(R.id.tvSub);
        btnStart = findViewById(R.id.btnStart);
        tvTime = findViewById(R.id.textView2);
        parentLayout = findViewById(R.id.parent);
        containerButton = findViewById(R.id.buttonContainer);

        parentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                containerButton.setVisibility(VISIBLE);
            }
        });
        containerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                containerButton.setVisibility(GONE);
            }
        });

        btnBackWard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.configTime(false, 500);
            }
        });
        btnForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.configTime(true, 500);
            }
        });
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

    private void getSubFromUrl(String url) {
        DownloadFile downLoadFile = new DownloadFile(mContext, new DownloadCallback() {
            @Override
            public void onDownload(File file) {
                try {
                    state = new State(0L, new ArrayList<Subtitle>(), new ArrayList<Timestamp>(), btnStart, timeLabel);
                    mHandler.setSubtitle(new FileInputStream(file), state);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    onError("Can't load subtitle!");
                }
            }

            @Override
            public void onFail(Exception e) {
                LogUtils.d("DownloadError", e.getMessage());
                onError("Can't load subtitle!");
            }
        });
        downLoadFile.execute(url, "sub.srt");
    }
}
