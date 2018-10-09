package com.nklight.ultsub.SubView;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.nklight.ultsub.Subtitle.InvalidTimestampFormatException;
import com.nklight.ultsub.Subtitle.Subtitle;
import com.nklight.ultsub.Subtitle.SubtitleFile;
import com.nklight.ultsub.Subtitle.Timestamp;
import com.nklight.ultsub.Utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class SubHandler extends Handler {

    private SubHandlerListener mListener;
    private Context mContext;
    private InputStream inputStream;
    private SubtitleFile subtitleFile;
    private ArrayList<Subtitle> subsWithPadding = new ArrayList<>();
    private Timer mTimer;
    // state
    private Long offsetoffsetMillis;
    private ArrayList<Subtitle> subtitles = new ArrayList<>();
    private ArrayList<Timestamp> startingTimestamps = new ArrayList<>();

    interface SubHandlerListener {
        void onTextChange(String text);

        void onError(String error);
    }

    public SubHandler(Looper looper, SubHandlerListener listener, Context context) {
        super(looper);
        this.mListener = listener;
        this.mContext = context;
    }

    private void initTimer() {
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Timestamp timestamp = Timestamp.Companion.fromTotalMillis(System.currentTimeMillis() - offsetoffsetMillis);
                LogUtils.d("timestamp", timestamp.compile());
            }
        };
    }

    public void setSubtitle(InputStream inputStream) {
        this.inputStream = inputStream;
        try {
            this.subtitleFile = new SubtitleFile(inputStream);
        } catch (IOException e) {
            mListener.onError("File not found");
        } catch (InvalidTimestampFormatException e) {
            mListener.onError("Some line not right!");
            //continue
        }

        subsWithPadding.add(new Subtitle(
                Timestamp.Companion.fromTotalMillis(0),
                Timestamp.Companion.fromTotalMillis(0),
                Collections.nCopies(30, "")));

        subsWithPadding.addAll(subtitleFile.getSubtitles());
        subtitles.clear();
        subtitles.addAll(subsWithPadding);
        for (Subtitle sub : subtitles) {
            startingTimestamps.add(sub.getStartTime());
        }

    }

    public void configTime(boolean addTime, int timeValue) {

    }

    public void start() {
        if (inputStream == null && mListener == null) return;


    }
}
