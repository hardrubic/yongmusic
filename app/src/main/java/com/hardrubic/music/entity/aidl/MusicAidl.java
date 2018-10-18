package com.hardrubic.music.entity.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

public class MusicAidl implements Parcelable {
    private Long musicId;
    private String name;
    private String path;
    private Integer duration;
    private Integer size;
    private String artistNames;
    private String albumName;
    private String coverUrl;

    public Long getMusicId() {
        return musicId;
    }

    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(String artistNames) {
        this.artistNames = artistNames;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.musicId);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeValue(this.duration);
        dest.writeValue(this.size);
        dest.writeString(this.artistNames);
        dest.writeString(this.albumName);
        dest.writeString(this.coverUrl);
    }

    public MusicAidl() {
    }

    protected MusicAidl(Parcel in) {
        this.musicId = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.path = in.readString();
        this.duration = (Integer) in.readValue(Integer.class.getClassLoader());
        this.size = (Integer) in.readValue(Integer.class.getClassLoader());
        this.artistNames = in.readString();
        this.albumName = in.readString();
        this.coverUrl = in.readString();
    }

    public static final Creator<MusicAidl> CREATOR = new Creator<MusicAidl>() {
        @Override
        public MusicAidl createFromParcel(Parcel source) {
            return new MusicAidl(source);
        }

        @Override
        public MusicAidl[] newArray(int size) {
            return new MusicAidl[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicAidl musicAidl = (MusicAidl) o;
        return Objects.equals(musicId, musicAidl.musicId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(musicId);
    }
}
