package com.nklight.ultsub.SubView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nklight.ultsub.R;
import com.nklight.ultsub.Subtitle.Subtitle;
import com.nklight.ultsub.Subtitle.Timestamp;
import com.nklight.ultsub.Utils.CompoundViewHelper;
import com.nklight.ultsub.Utils.DownloadCallback;
import com.nklight.ultsub.Utils.DownloadFile;
import com.nklight.ultsub.Utils.DownloadWithUnzip;
import com.nklight.ultsub.Utils.FileUtils;
import com.nklight.ultsub.Utils.LogUtils;
import com.nklight.ultsub.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubView extends CompoundViewHelper.ConstraintLayout implements SubHandler.SubHandlerListener, DownloadWithUnzip.OnDownloadStatusListener {

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
    private Button btnLoad;
    private ConstraintLayout parentLayout;
    private ConstraintLayout containerButton;
    private ProgressDialog mProgressDialog;
    private AlertDialog mLinkDialog;
    private String mFilePath;


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
        btnLoad = findViewById(R.id.btnLoad);
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
        btnLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openInputLinkDialog();
            }
        });
    }

    private void createInputLinkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Input Link");
        final EditText input = new EditText(mContext);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadAndUnzip(input.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mLinkDialog = builder.create();
        mLinkDialog.getWindow().setType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    private void openInputLinkDialog() {
        createInputLinkDialog();
        this.post(new Runnable() {
            @Override
            public void run() {
                if (state.toggleFollow.isChecked()) {
                    state.toggleFollow.performClick();
                }
                mLinkDialog.show();
            }
        });
    }

    private void downloadAndUnzip(String link) {
        if (!URLUtil.isValidUrl(link)) {
            onError("Not valid link");
            return;
        }
        DownloadWithUnzip.FileDownloader downloader = DownloadWithUnzip.FileDownloader.getInstance(createDownloadRequest(link), SubView.this);
        downloader.download(mContext);
    }


    private void setDialog(final boolean isShow) {
        if (mProgressDialog == null) createProgressDialog();
        this.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    mProgressDialog.show();
                } else {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void setDialog(final boolean isShow, final int percent) {
        if (mProgressDialog == null) createProgressDialog();
        this.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    mProgressDialog.show();
                } else {
                    mProgressDialog.dismiss();
                }
                mProgressDialog.setProgress(percent);
            }
        });
    }

    private void setDialogPercent(final int percent) {
        if (mProgressDialog == null) createProgressDialog();
        this.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setProgress(percent);
            }
        });
    }


    private void createProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Downloading");
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.getWindow().setType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }


    @Override
    public void onTextChange(final String text, final String time) {
        this.post(new Runnable() {
            @Override
            public void run() {
                tvSub.setText(text);
                tvTime.setText(time);

            }
        });
    }

    @Override
    public void onTextChange(final int text, final String time) {
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
    public void onError(final int error) {
        this.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTextChange(final List<String> texts, final String time) {
        this.post(new Runnable() {
            @Override
            public void run() {
                tvSub.setText(Utils.joinToString(texts));
                tvTime.setText(time);
            }
        });

    }

    private void getSubFromUrl(String url) {
        setDialog(true);
        DownloadFile downLoadFile = new DownloadFile(mContext, new DownloadCallback() {
            @Override
            public void onDownload(File file) {
                try {
                    state = new State(0L, new ArrayList<Subtitle>(), new ArrayList<Timestamp>(), btnStart, timeLabel);
                    mHandler.setSubtitle(new FileInputStream(file), state);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    onError(R.string.er_cant_load);
                }
                setDialog(false);
            }

            @Override
            public void onFail(Exception e) {
                LogUtils.d("DownloadError", e.getMessage());
                onError(R.string.er_cant_load);
                setDialog(false);
            }

            @Override
            public void onProgressUpdate(String percent) {
                setDialogPercent(Integer.valueOf(percent));
            }
        });
        downLoadFile.execute(url, "sub");
    }

    private DownloadWithUnzip.DownloadRequest createDownloadRequest(String link) {
        String path = FileUtils.getDataDir(mContext).getAbsolutePath();
        String fileName = "subZ";
        File file = new File(path, fileName);
        String localPath = file.getAbsolutePath();
        String unzipPath = FileUtils.getDataDir(mContext, "ExtractSub").getAbsolutePath();
        mFilePath = unzipPath;
        DownloadWithUnzip.DownloadRequest result = new DownloadWithUnzip.DownloadRequest(link, localPath);
        result.setRequiresUnzip(true);
        result.setDeleteZipAfterExtract(false);
        result.setUnzipAtFilePath(unzipPath);
        return result;
    }

    @Override
    public void onDownloadStarted() {
        setDialog(true, 0);
    }

    @Override
    public void onDownloadCompleted() {
        setDialog(false);
        onError(R.string.if_download_complete);
        try {
            File folder = new File(mFilePath);
            File[] file = folder.listFiles();
            inputStream = new FileInputStream(file[0]);
            state = new State(0L, new ArrayList<Subtitle>(), new ArrayList<Timestamp>(), btnStart, timeLabel);
            mHandler.setSubtitle(inputStream, state);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadFailed() {
        setDialog(false);
        onError(R.string.if_download_fail);
    }

    @Override
    public void onDownloadProgress(int progress) {
        setDialogPercent(progress);
    }
}
