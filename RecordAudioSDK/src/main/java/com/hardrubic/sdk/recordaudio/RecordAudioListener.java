package com.hardrubic.sdk.recordaudio;

public interface RecordAudioListener {

    void refreshPlayState();

    void onTimeChange(long time);

    void complete();
}