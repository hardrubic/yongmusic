package com.hardrubic.music.db.dataobject;

import com.hardrubic.music.network.converter.AliasArrayConverter;
import java.util.List;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Artist {
    @Id
    private Long artistId;
    private String name;
    private String picUrl;
    @Convert(converter = AliasArrayConverter.class,columnType = String.class)
    private List<String> alias;
    @Generated(hash = 1607945390)
    public Artist(Long artistId, String name, String picUrl, List<String> alias) {
        this.artistId = artistId;
        this.name = name;
        this.picUrl = picUrl;
        this.alias = alias;
    }
    @Generated(hash = 19829037)
    public Artist() {
    }
    public Long getArtistId() {
        return this.artistId;
    }
    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPicUrl() {
        return this.picUrl;
    }
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public List<String> getAlias() {
        return this.alias;
    }
    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

}
