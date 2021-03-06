package com.nklight.ultsub.SubView;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ToggleButton;

import com.nklight.ultsub.R;
import com.nklight.ultsub.Subtitle.InvalidTimestampFormatException;
import com.nklight.ultsub.Subtitle.Subtitle;
import com.nklight.ultsub.Subtitle.SubtitleFile;
import com.nklight.ultsub.Subtitle.Timestamp;
import com.nklight.ultsub.Utils.LogUtils;
import com.nklight.ultsub.Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SubHandler extends Handler {

    private SubHandlerListener mListener;
    private Context mContext;
    private InputStream mInputStream;
    private SubtitleFile mSubtitleFile;
    private ArrayList<Subtitle> mSubsWithPadding = new ArrayList<>();
    private ToggleButton play;
    // state
    private State state;
    private String timeLabel;

    interface SubHandlerListener {
        void onTextChange(String text, String time);

        void onTextChange(@StringRes int text, String time);

        void onError(String error);

        void onError(@StringRes int error);

        void onTextChange(List<String> texts, String time);
    }

    public SubHandler(Looper looper, SubHandlerListener listener, Context context, ToggleButton controlButton, String timeLabel) {
        super(looper);
        this.mListener = listener;
        this.mContext = context;
        this.play = controlButton;
        this.timeLabel = timeLabel;
    }


    public void setSubtitle(InputStream inputStream, State state) {
        this.mInputStream = inputStream;
        try {
            this.mSubtitleFile = new SubtitleFile(inputStream);
        } catch (IOException e) {
            mListener.onError(R.string.er_file_not_found);
        } catch (InvalidTimestampFormatException e) {
            mListener.onError(R.string.er_cant_read);
            return;
        }
        mSubsWithPadding.clear();

        mSubsWithPadding.add(new Subtitle(
                Timestamp.Companion.fromTotalMillis(0),
                Timestamp.Companion.fromTotalMillis(0),
                Collections.nCopies(30, "")));

        mSubsWithPadding.addAll(mSubtitleFile.getSubtitles());
        state.subtitles.clear();
        state.subtitles.addAll(mSubsWithPadding);
        for (Subtitle sub : state.subtitles) {
            state.startingTimestamps.add(sub.getStartTime());
        }
        init(state);

    }

    public void configTime(boolean addTime, int timeValueInMillis) {
        this.state.offsetMillis = addTime ?
                state.offsetMillis - timeValueInMillis : state.offsetMillis + timeValueInMillis;
        if (!state.toggleFollow.isChecked()) {
            Timestamp timestamp = Timestamp.Companion.fromTotalMillis(System.currentTimeMillis() - state.offsetMillis);
            timeLabel = timestamp.compile();
            state.timeLabel = timeLabel;
            mListener.onTextChange(R.string.txt_empty, timeLabel);
        }
    }

    public void start() {
        if (mInputStream == null && mListener == null) return;


    }

    private void init(final State state) {
        this.state = state;
        if (mInputStream == null) {
            mListener.onError(R.string.er_not_found);
            return;
        }

        // load sub to state
        final Timer[] timer = {null};

        state.toggleFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {
                    timer[0] = new Timer();
                    TimerTask advanceTime = new TimerTask() {
                        @Override
                        public void run() {
                            Timestamp timestamp = Timestamp.Companion.fromTotalMillis(System.currentTimeMillis() - state.offsetMillis);
                            // set time to show = timestamp.compile()
                            timeLabel = timestamp.compile();
                            state.timeLabel = timeLabel;
                            int binSearchIndex = Collections.binarySearch(state.startingTimestamps, timestamp);
                            int subtitleToScrollTo;
                            if (binSearchIndex > 0) {
                                if (binSearchIndex < 1) {
                                    subtitleToScrollTo = 1;
                                } else {
                                    subtitleToScrollTo = binSearchIndex;
                                }
                            } else {
                                int previousItem = -2 - binSearchIndex;
                                if (previousItem < 1) {
                                    subtitleToScrollTo = 1;
                                } else {
                                    subtitleToScrollTo = previousItem;
                                }
                            }
                            Subtitle currentSub = state.subtitles.get(subtitleToScrollTo);
                            double progress = (1.0 * (timestamp.getTotalMillis() - currentSub.getStartTime().getTotalMillis())
                                    / (currentSub.getEndTime().getTotalMillis() - currentSub.getStartTime().getTotalMillis()));
                            if (progress > 1.0 || progress < 0) {
                                //hide sub
                                mListener.onTextChange(R.string.txt_empty, timeLabel);
                                LogUtils.d("showSub", "text = notext");
                            } else {
                                //show sub
                                mListener.onTextChange(currentSub.getLines(), timeLabel);
                                LogUtils.d("showSub", "text = " + Utils.joinToString(currentSub.getLines()));
                            }
                        }
                    };

                    timer[0].schedule(advanceTime, 100, 100);
                    try {
                        state.offsetMillis = System.currentTimeMillis() - new Timestamp(state.timeLabel).getTotalMillis();
                    } catch (InvalidTimestampFormatException e) {
                        e.printStackTrace();
                        mListener.onError(R.string.er_something);
                        LogUtils.d("Error", "set state.offsetMillis fail");
                    }


                } else {
                    timer[0].cancel();
                }
            }
        });
    }
}

class State {
    public Long offsetMillis;
    public List<Subtitle> subtitles;
    public List<Timestamp> startingTimestamps;
    public ToggleButton toggleFollow;
    public String timeLabel;

    public State(Long offsetMillis, List<Subtitle> subtitles, List<Timestamp> startingTimestamps, ToggleButton play, String timeLabel) {
        this.offsetMillis = offsetMillis;
        this.subtitles = subtitles;
        this.startingTimestamps = startingTimestamps;
        this.toggleFollow = play;
        this.timeLabel = timeLabel;
    }
}
