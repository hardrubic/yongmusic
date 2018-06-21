package com.hardrubic.music.db.dataobject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MusicCollectionRelation {
    @Id
    private Long id;
    private Long collectionId;
    private Long musicId;
    private Integer order;
    @Generated(hash = 1187925485)
    public MusicCollectionRelation(Long id, Long collectionId, Long musicId,
            Integer order) {
        this.id = id;
        this.collectionId = collectionId;
        this.musicId = musicId;
        this.order = order;
    }
    @Generated(hash = 13395117)
    public MusicCollectionRelation() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCollectionId() {
        return this.collectionId;
    }
    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
    public Long getMusicId() {
        return this.musicId;
    }
    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }
    public Integer getOrder() {
        return this.order;
    }
    public void setOrder(Integer order) {
        this.order = order;
    }



}
