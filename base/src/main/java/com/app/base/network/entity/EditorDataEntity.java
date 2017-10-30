package com.app.base.network.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.app.base.adapter.entity.MultiItemEntity;

/**
 * desc：编辑器中数据实体
 * author：mgq
 * date：2017-06-06
 */

public class EditorDataEntity implements Parcelable, MultiItemEntity {
    public static final String TYPE_TEXT = "textTag";
    public static final String TYPE_VIDEO = "videoTag";
    public static final String TYPE_AUDIO = "audioTag";
    public static final String TYPE_IMAGE = "imageTag";
    public static final String TYPE_ALINK = "alinkTag";

    /*当前资源所在的位置*/
    private int position;
    /*当前的资源类型*/
    private String type;
    /*文本*/
    private String text;
    /*url:图片、音频、视频路径*/
    private String url;
    /*图片资源*/
    private Bitmap bitmapResource;
    /*音频视频的请求地址:对应多媒体标签的src*/
    private String mediaPath;
    /*音频视频的源文件名*/
    private String name;
    /*文件服务器对应的文件唯一别名：对应多媒体标签的aliasname*/
    private String aliasname;
    /*文件服务器的存储路径，需要去掉upFile后存入*/
    private String path;
    /*多媒体文件的时长*/
    private String mediaDuration = "0";
    /*视频缩略图地址*/
    private String poster;

    private int itemType;

    public EditorDataEntity() {
    }

    public EditorDataEntity(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public EditorDataEntity(String text, String type, String url, Bitmap bitmapResource) {
        this.text = text;
        this.type = type;
        this.url = url;
        this.bitmapResource = bitmapResource;
    }

    protected EditorDataEntity(Parcel in) {
        position = in.readInt();
        type = in.readString();
        text = in.readString();
        url = in.readString();
        bitmapResource = in.readParcelable(Bitmap.class.getClassLoader());
        mediaPath = in.readString();
        name = in.readString();
        aliasname = in.readString();
        path = in.readString();
        mediaDuration = in.readString();
        poster = in.readString();
        itemType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeString(type);
        dest.writeString(text);
        dest.writeString(url);
        dest.writeParcelable(bitmapResource, flags);
        dest.writeString(mediaPath);
        dest.writeString(name);
        dest.writeString(aliasname);
        dest.writeString(path);
        dest.writeString(mediaDuration);
        dest.writeString(poster);
        dest.writeInt(itemType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EditorDataEntity> CREATOR = new Creator<EditorDataEntity>() {
        @Override
        public EditorDataEntity createFromParcel(Parcel in) {
            return new EditorDataEntity(in);
        }

        @Override
        public EditorDataEntity[] newArray(int size) {
            return new EditorDataEntity[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmapResource() {
        return bitmapResource;
    }

    public void setBitmapResource(Bitmap bitmapResource) {
        this.bitmapResource = bitmapResource;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasname() {
        return aliasname;
    }

    public void setAliasname(String aliasname) {
        this.aliasname = aliasname;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMediaDuration() {
        return mediaDuration;
    }

    public void setMediaDuration(String mediaDuration) {
        this.mediaDuration = mediaDuration;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return "EditorDataEntity{" +
                "position=" + position +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", url='" + url + '\'' +
                ", bitmapResource=" + bitmapResource +
                ", mediaPath='" + mediaPath + '\'' +
                ", name='" + name + '\'' +
                ", aliasname='" + aliasname + '\'' +
                ", path='" + path + '\'' +
                ", mediaDuration='" + mediaDuration + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }

    @Override
    public int getItemType() {
        switch (type) {
            case TYPE_TEXT:
                return 0;
            case TYPE_IMAGE:
                return 1;
            case TYPE_AUDIO:
                return 2;
            case TYPE_VIDEO:
                return 3;
            case TYPE_ALINK:
                return 4;
        }
        return itemType;
    }
}
