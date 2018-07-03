package com.hardrubic.music.db.dataobject;

import com.hardrubic.music.network.converter.IdsConverter;
import com.hardrubic.music.network.converter.NamesConverter;
import java.util.List;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Music {
    @Id
    private Long musicId;
    private String name;
    private String path;
    @Convert(converter = IdsConverter.class, columnType = String.class)
    private List<Long> artistIds;
    @Convert(converter = NamesConverter.class, columnType = String.class)
    private List<String> artistNames;
    private Long albumId;
    private String albumName;
    private Integer duration;
    private Boolean local;//本地music
    private Boolean download;//下载music

    @Transient
    private List<Artist> artists;
    @Transient
    private Album album;

    @Generated(hash = 471745276)
    public Music(Long musicId, String name, String path, List<Long> artistIds,
            List<String> artistNames, Long albumId, String albumName,
            Integer duration, Boolean local, Boolean download) {
        this.musicId = musicId;
        this.name = name;
        this.path = path;
        this.artistIds = artistIds;
        this.artistNames = artistNames;
        this.albumId = albumId;
        this.albumName = albumName;
        this.duration = duration;
        this.local = local;
        this.download = download;
    }

    @Generated(hash = 1263212761)
    public Music() {
    }

    @Keep
    public List<Artist> getArtists() {
        return artists;
    }

    @Keep
    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    @Keep
    public Album getAlbum() {
        return album;
    }

    @Keep
    public void setAlbum(Album album) {
        this.album = album;
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

    public List<Long> getArtistIds() {
        return this.artistIds;
    }

    public void setArtistIds(List<Long> artistIds) {
        this.artistIds = artistIds;
    }

    public List<String> getArtistNames() {
        return this.artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }

    public Long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
