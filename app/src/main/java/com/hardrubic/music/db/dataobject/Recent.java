package com.hardrubic.music.db.dataobject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

@Entity
public class Recent {
    @Id
    private Long musicId;
    private Long lastTime;
    private Integer count;

    @ToOne(joinProperty = "musicId")
    private Music music;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1225945926)
    private transient RecentDao myDao;
    @Generated(hash = 993622651)
    private transient Long music__resolvedKey;

    @Generated(hash = 306247849)
    public Recent(Long musicId, Long lastTime, Integer count) {
        this.musicId = musicId;
        this.lastTime = lastTime;
        this.count = count;
    }

    @Generated(hash = 1212650171)
    public Recent() {
    }

    public Long getMusicId() {
        return this.musicId;
    }

    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }

    public Long getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1361111467)
    public Music getMusic() {
        Long __key = this.musicId;
        if (music__resolvedKey == null || !music__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MusicDao targetDao = daoSession.getMusicDao();
            Music musicNew = targetDao.load(__key);
            synchronized (this) {
                music = musicNew;
                music__resolvedKey = __key;
            }
        }
        return music;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1915972305)
    public void setMusic(Music music) {
        synchronized (this) {
            this.music = music;
            musicId = music == null ? null : music.getMusicId();
            music__resolvedKey = musicId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 952737433)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecentDao() : null;
    }


}
