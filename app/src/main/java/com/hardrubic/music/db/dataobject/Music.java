package com.hardrubic.music.db.dataobject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Music {
    @Id
    private Long musicId;
    private String name;
    private String path;
    private String artist;
    private Integer duration;
    private Integer size;
    private Boolean local;//本地music
    private Boolean download;//下载music
    @Generated(hash = 1247652276)
    public Music(Long musicId, String name, String path, String artist,
            Integer duration, Integer size, Boolean local, Boolean download) {
        this.musicId = musicId;
        this.name = name;
        this.path = path;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        this.local = local;
        this.download = download;
    }
    @Generated(hash = 1263212761)
    public Music() {
    }
    public Long getMusicId() {
        return this.musicId;
    }
    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public Integer getDuration() {
        return this.duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Integer getSize() {
        return this.size;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public Boolean getLocal() {
        return this.local;
    }
    public void setLocal(Boolean local) {
        this.local = local;
    }
    public Boolean getDownload() {
        return this.download;
    }
    public void setDownload(Boolean download) {
        this.download = download;
    }



}
