package com.hardrubic.sdk.recordaudio;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.hardrubic.sdk.recordaudio.record.MP3Recorder;
import com.hardrubic.sdk.recordaudio.ui.AudioWaveView;
import java.io.File;
import java.io.IOException;

public class RecordAudioManager {

    private MP3Recorder mRecorder;
    private AudioWaveView audioWaveView;
    private RecordAudioListener recordAudioListener;

    public RecordAudioManager() {

    }

    public void start(String fileName, AudioWaveView audioWaveView, RecordAudioListener listener) {
        this.recordAudioListener = listener;
        this.audioWaveView = audioWaveView;

        mRecorder = new MP3Recorder(new File(fileName));
        mRecorder.initVolumeDataSave(audioWaveView.getRecList());
        mRecorder.setTimeHander(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MP3Recorder.MSG_TIME_CHANGE:
                        long time = (long) msg.obj;
                        if (null != recordAudioListener) {
                            recordAudioListener.onTimeChange(time);
                        }
                        break;
                    case MP3Recorder.MSG_COMPLETE_ENCODING:
                        if (null != recordAudioListener) {
                            recordAudioListener.complete();
                        }
                        break;
                }
            }
        });

        try {
            mRecorder.start();
            audioWaveView.startDraw();
            if (null != recordAudioListener) recordAudioListener.refreshPlayState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (null == mRecorder || null == audioWaveView) return;
        if (mRecorder.isRecording()) mRecorder.stop();
        if (audioWaveView.isPlaying()) {
            audioWaveView.stopDraw();
        }
        if (null != recordAudioListener) recordAudioListener.refreshPlayState();
    }

    public void stopDrawing() {
        if (audioWaveView.isPlaying()) audioWaveView.stopDraw();
    }

    public boolean isRecording() {
        if (mRecorder == null) {
            return false;
        }
        return mRecorder.isRecording();
    }

    public void release() {
        mRecorder.release();
        if (audioWaveView != null) audioWaveView.release();
    }

    public void delete(String fileName) {
        stop();
        if (TextUtils.isEmpty(fileName)) return;
        File file = new File(fileName);
        if (file.exists()) file.delete();
    }
}
