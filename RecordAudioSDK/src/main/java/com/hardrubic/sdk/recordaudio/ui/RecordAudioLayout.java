package com.hardrubic.sdk.recordaudio.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hardrubic.sdk.recordaudio.R;
import com.hardrubic.sdk.recordaudio.RecordAudioListener;
import com.hardrubic.sdk.recordaudio.RecordAudioManager;
import com.hardrubic.sdk.recordaudio.record.MP3Recorder;

public class RecordAudioLayout extends LinearLayout {
    private Context mContext;
    private RecordAudioManager manager;
    private ImageView iv_play_status;
    private TextView tv_timer;
    private AudioWaveView audioWave;
    private RecordAudioCompletedListener listener;
    private String tmpRecordPath;

    public RecordAudioLayout(Context context) {
        super(context);
        init(context, null);
    }

    public RecordAudioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecordAudioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_record_audio, null);

        iv_play_status = view.findViewById(R.id.iv_play_status);
        iv_play_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager == null || !manager.isRecording()) {
                    startRecord();
                } else {
                    stopRecord();
                }
            }
        });

        tv_timer = view.findViewById(R.id.tv_timer);

        audioWave = view.findViewById(R.id.audioWave);
        this.addView(view);
    }

    private RecordAudioListener recordAudioListener = new RecordAudioListener() {
        @Override
        public void refreshPlayState() {
            if (manager.isRecording()) {
                iv_play_status.setBackgroundResource(R.mipmap.stop);
            } else {
                iv_play_status.setBackgroundResource(R.mipmap.play);
            }
        }

        /**
         * 中间会有毫秒的差别忽略不计
         * 每次显示的时候将数据恢复到初始状态
         */
        @Override
        public void onTimeChange(long time) {
            if (time >= MP3Recorder.MAX_DURATION) {
                refreshPlayState();
                tv_timer.setText(MP3Recorder.MAX_DURATION / 1000 + ":00");
                manager.stopDrawing();
            } else {
                StringBuffer timing = new StringBuffer();
                int second = (int) (time / 1000);
                if (second < 10) {
                    timing.append("0" + second + ":");
                } else {
                    timing.append(second + ":");
                }
                int millSecond = (int) (time % 100);
                if (millSecond < 10) {
                    timing.append("0" + millSecond);
                } else {
                    timing.append(millSecond);
                }
                tv_timer.setText(timing);
            }
        }

        @Override
        public void complete() {
            if (null != listener) {
                reset();
                listener.completeRecord(tmpRecordPath);
            }
        }
    };

    public void startRecord() {
        manager = new RecordAudioManager();
        if (!manager.isRecording() && null != tmpRecordPath) {
            manager.start(tmpRecordPath, audioWave, recordAudioListener);
        }
    }

    public void stopRecord() {
        manager.stop();
    }

    private void reset() {
        tv_timer.setText("00:00:00");
    }

    public void setTmpRecordPath(String path) {
        this.tmpRecordPath = path;
    }

    public void setRecordAudioCompletedListener(RecordAudioCompletedListener listener) {
        this.listener = listener;
    }

    public interface RecordAudioCompletedListener {
        void completeRecord(String path);
    }
}
