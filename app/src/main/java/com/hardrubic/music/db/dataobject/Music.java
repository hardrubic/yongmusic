package com.hardrubic.music.db.dataobject;

import com.hardrubic.music.network.converter.IdsConverter;
import com.hardrubic.music.network.converter.NamesConverter;
import java.util.List;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

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

    @ToOne(joinProperty = "albumId")
    private Album album;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1255683360)
    private transient MusicDao myDao;
    @Generated(hash = 381107260)
    private transient Long album__resolvedKey;

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

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1001254595)
    public Album getAlbum() {
        Long __key = this.albumId;
        if (album__resolvedKey == null || !album__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AlbumDao targetDao = daoSession.getAlbumDao();
            Album albumNew = targetDao.load(__key);
            synchronized (this) {
                album = albumNew;
                album__resolvedKey = __key;
            }
        }
        return album;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 770122782)
    public void setAlbum(Album album) {
        synchronized (this) {
            this.album = album;
            albumId = album == null ? null : album.getAlbumId();
            album__resolvedKey = albumId;
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
    @Generated(hash = 1218270154)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMusicDao() : null;
    }

}
