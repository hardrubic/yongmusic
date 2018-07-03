package com.hardrubic.music.db.dataobject;

import com.hardrubic.music.network.converter.AliasArrayConverter;
import com.hardrubic.music.network.converter.IdsConverter;
import com.hardrubic.music.network.converter.NamesConverter;
import java.util.List;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Album {
    @Id
    private Long albumId;
    private String name;
    @Convert(converter = IdsConverter.class, columnType = String.class)
    private List<Long> artistIds;
    @Convert(converter = NamesConverter.class, columnType = String.class)
    private List<String> artistNames;
    @Convert(converter = AliasArrayConverter.class, columnType = String.class)
    private List<String> alias;
    private Long publishTime;
    private String picUrl;

    @Transient
    private List<Artist> artists;

    @Keep
    public List<Artist> getArtists() {
        return artists;
    }

    @Keep
    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }


    @Generated(hash = 599402441)
    public Album(Long albumId, String name, List<Long> artistIds,
                 List<String> artistNames, List<String> alias, Long publishTime,
                 String picUrl) {
        this.albumId = albumId;
        this.name = name;
        this.artistIds = artistIds;
        this.artistNames = artistNames;
        this.alias = alias;
        this.publishTime = publishTime;
        this.picUrl = picUrl;
    }

    @Generated(hash = 1609191978)
    public Album() {
    }

    public Long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getAlias() {
        return this.alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public Long getPublishTime() {
        return this.publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
