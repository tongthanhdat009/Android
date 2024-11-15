package com.project.myapplication.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Map;

public class ChatBox implements Parcelable {
    private String Id;
    private String name;
    private Map<String, Boolean> showed;
    private Timestamp lastMessageTimestamp;
    private String lastMessage;
    private String image_url;

    public ChatBox() {}

    public ChatBox(String id, String name, Map<String, Boolean> showed, Timestamp lastMessageTimestamp, String lastMessage, String image_url) {
        Id = id;
        this.name = name;
        this.showed = showed;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessage = lastMessage;
        this.image_url = image_url;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public void setShowed(Map<String, Boolean> showed) {
        this.showed = showed;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getShowed() {
        return showed;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getImageUrl() {
        return image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(name);
        dest.writeMap(showed);
        dest.writeParcelable(lastMessageTimestamp, flags);  // Firebase Timestamp should be written as Parcelable
        dest.writeString(lastMessage);
        dest.writeString(image_url);
    }

    public static final Creator<ChatBox> CREATOR = new Creator<ChatBox>() {
        @Override
        public ChatBox createFromParcel(Parcel in) {
            return new ChatBox(in);
        }

        @Override
        public ChatBox[] newArray(int size) {
            return new ChatBox[size];
        }
    };

    protected ChatBox(Parcel in) {
        Id = in.readString();
        name = in.readString();
        showed = in.readHashMap(Map.class.getClassLoader());
        lastMessageTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMessage = in.readString();
        image_url = in.readString();
    }

    @Override
    public String toString() {
        return "ChatBox{" +
                "Id='" + Id + '\'' +
                ", name='" + name + '\'' +
                ", showed=" + showed +
                ", lastMessageTimestamp=" + lastMessageTimestamp +
                ", lastMessage='" + lastMessage + '\'' +
                ", image_Url='" + image_url + '\'' +
                '}';
    }
}