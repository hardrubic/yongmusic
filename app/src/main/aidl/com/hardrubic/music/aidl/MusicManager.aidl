package com.hardrubic.music.aidl;

import com.hardrubic.music.aidl.MusicAidl;

interface MusicManager {
    void play();

    void pause();

    void stop();

    void next();

    void previous();

    void seekTo(int position);

    boolean isPlaying();

    void select(in MusicAidl music);

    void playList(in List<MusicAidl> musics);

    void applyCurrentMusic();

    void applyPlayState();

    void applyPlayModel(int playModel);
}
