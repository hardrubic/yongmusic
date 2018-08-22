package com.hardrubic.music;

import com.hardrubic.music.entity.aidl.MusicAidl;

interface MusicManager {
    void play();

    void pause();

    void stop();

    void next();

    void previous();

    void seekTo(int position);

    boolean isPlaying();

    void select(in MusicAidl music);

    void musics(in List<MusicAidl> musics);

    void updatePlayModel(int playModel);

    void applyCurrentMusic();

    void applyPlayState();
}
