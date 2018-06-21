package com.hardrubic.music.db.dataobject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Collection {
    @Id
    private Long id;
    private String name;
    private Integer resId;
    private Long createdAt;
    private String desc;

    @Transient
    private Integer musicNum;

    @Generated(hash = 1145291547)
    public Collection(Long id, String name, Integer resId, Long createdAt,
            String desc) {
        this.id = id;
        this.name = name;
        this.resId = resId;
        this.createdAt = createdAt;
        this.desc = desc;
    }
    @Generated(hash = 1149123052)
    public Collection() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getResId() {
        return this.resId;
    }
    public void setResId(Integer resId) {
        this.resId = resId;
    }
    public Long getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    public String getDesc() {
        return this.desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getMusicNum() {
        return musicNum;
    }

    public void setMusicNum(Integer musicNum) {
        this.musicNum = musicNum;
    }
}
