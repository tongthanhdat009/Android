package com.project.myapplication.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class ChatBox implements Parcelable {
    private String Id;
    private Map<String, String> name;
    private Map<String, Boolean> showed;
    private Timestamp lastMessageTimestamp;
    private String lastMessage;
    private Map<String, String> image_url;

    public ChatBox() {}

    public ChatBox(String id, Map<String, String> name, Map<String, Boolean> showed, Timestamp lastMessageTimestamp, String lastMessage, Map<String, String> image_url) {
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

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public void setImageUrl(Map<String, String> imageUrl) {
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

    public Map<String, String> getName() {
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

    public Map<String, String> getImageUrl() {
        return image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeSerializable((HashMap<String, String>) name);
        dest.writeSerializable((HashMap<String, Boolean>) showed);

        // Ghi Timestamp thành 2 giá trị
        if (lastMessageTimestamp != null) {
            dest.writeLong(lastMessageTimestamp.getSeconds());
            dest.writeInt(lastMessageTimestamp.getNanoseconds());
        } else {
            dest.writeLong(0);
            dest.writeInt(0);
        }

        dest.writeString(lastMessage);
        dest.writeSerializable((HashMap<String, String>) image_url);
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
        name = (HashMap<String, String>) in.readSerializable();
        showed = (HashMap<String, Boolean>) in.readSerializable();

        // Đọc Timestamp từ 2 giá trị
        long seconds = in.readLong();
        int nanoseconds = in.readInt();
        lastMessageTimestamp = new Timestamp(seconds, nanoseconds);

        lastMessage = in.readString();
        image_url = (HashMap<String, String>) in.readSerializable();
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatBox{" +
                "Id='" + Id + '\'' +
                ", name=" + name +
                ", showed=" + showed +
                ", lastMessageTimestamp=" + lastMessageTimestamp +
                ", lastMessage='" + lastMessage + '\'' +
                ", image_url=" + image_url +
                '}';
    }
}
