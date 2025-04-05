package com.project.myapplication.model;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatBoxModel {
    private final FirebaseFirestore firestore;

    public ChatBoxModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface ChatBoxCallback {
        void onCallback(List<ChatBox> chatBoxList);
    }

    public void getChatBoxesByUserID(String userID, ChatBoxCallback callback) {
        firestore.collection("chatbox")
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        List<ChatBox> filteredList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            ChatBox chatBox = document.toObject(ChatBox.class);
                            Log.d("test", document.toString());
                            Map<String, Object> rawImageUrls = (Map<String, Object>) document.get("image_url");
                            Map<String, String> imageUrls = new HashMap<>();

                            if (rawImageUrls != null) {
                                for (Map.Entry<String, Object> entry : rawImageUrls.entrySet()) {
                                    imageUrls.put(entry.getKey(), entry.getValue().toString()); // Chuyển value về String
                                }
                            }
                            chatBox.setImageUrl(imageUrls);
                            assert chatBox != null;

                            if (chatBox.getImageUrl() != null) {
                                for (Map.Entry<String, String> entry : chatBox.getImageUrl().entrySet()) {
                                    Log.d("image_url", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
                                }
                            } else {
                                Log.d("image_url", "image_url is null");
                            }

                            if (chatBox.getShowed() != null) {
                                for (Map.Entry<String, Boolean> entry : chatBox.getShowed().entrySet()) {
                                    if (entry.getKey().equals(userID) && entry.getValue()) {
                                        chatBox.setId(document.getId());
                                        filteredList.add(chatBox);
                                        Log.d("anh", filteredList.toString());
                                        break;
                                    }
                                }
                            }
                        }
                        callback.onCallback(filteredList);  // Trả về kết quả sau khi lọc
                    }
                });
    }

    public void hideChatBox(String chatBoxId, String userID) {
        firestore.collection("chatbox")
                .document(chatBoxId)
                .update("showed." + userID, false)
                .addOnSuccessListener(aVoid -> Log.d("ChatBoxModel", "ChatBox hidden successfully"))
                .addOnFailureListener(e -> Log.e("ChatBoxModel", "Error hiding chatbox", e));
    }

    public interface OnFollowingListRetrievedListener {
        void onRetrieved(List<String> userIds);
    }

    public void getListFollowingUser(String userID, OnFollowingListRetrievedListener listener) {
        firestore.collection("users")
                .document(userID)
                .collection("following")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> listFollowingUserIds = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Following following = document.toObject(Following.class);
                        if (following != null) {
                            listFollowingUserIds.add(following.getUserID());
                        }
                    }
                    listener.onRetrieved(listFollowingUserIds); // Gọi callback sau khi có danh sách userID
                });
    }

    public interface OnUsersInfoRetrievedListener {
        void onRetrieved(List<User> users);
    }

    public void getUsersInfo(List<String> userIds, OnUsersInfoRetrievedListener listener) {
        firestore.collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        assert user != null;
                        user.setUserID(document.getId());
                        users.add(user);
                    }
                    listener.onRetrieved(users); // Gọi callback sau khi có danh sách User
                });
    }

    public interface AvatarCallback {
        void onCallback(String avatarUrl);
    }

    public void getAvatar(String userId, AvatarCallback callback) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            String avatarUrl = user.getAvatar();
                            callback.onCallback(avatarUrl);
                        }
                    }
                });
    }

    public void updateLastMessage(String chatBoxId, String message) {
        DocumentReference chatBoxRef = firestore.collection("chatbox").document(chatBoxId);

        chatBoxRef.update(
                        "lastMessage", message,
                        "lastMessageTimestamp", FieldValue.serverTimestamp()
                )
                .addOnFailureListener(e -> {
                    Log.e("ChatBox", "Lỗi khi cập nhật lastMessage và lastMessageTimestamp", e);
                });
    }

    public void createOrUpdateChatBox(String userID1, String userID2, ChatBoxCallback2 callback) {
        firestore.collection("chatbox").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> showed = (Map<String, Object>) document.get("showed");

                        if (showed != null && showed.containsKey(userID1) && showed.containsKey(userID2)) {
                            // Nếu show chứa cả userID1 và userID2, cập nhật
                            String chatBoxId = document.getId();
                            updateShowedToTrue(chatBoxId, userID1, userID2, callback);
                            return;
                        }
                    }
                    // Nếu không tìm thấy chatbox, tạo mới
                    createNewChatBox(userID1, userID2, callback);
                });
    }

    public void createNewChatBox(String userID1, String userID2, ChatBoxCallback2 callback) {
        // Lấy tên của hai người dùng
        firestore.collection("users").document(userID1).get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    String userName1 = documentSnapshot1.exists() ? documentSnapshot1.getString("Name") : userID1;
                    String avatar1 = documentSnapshot1.exists() ? documentSnapshot1.getString("avatar") : null;

                    firestore.collection("users").document(userID2).get()
                            .addOnSuccessListener(documentSnapshot2 -> {
                                String userName2 = documentSnapshot2.exists() ? documentSnapshot2.getString("Name") : userID2;
                                String avatar2 = documentSnapshot2.exists() ? documentSnapshot2.getString("avatar") : null;

                                Map<String, Object> chatBoxData = new HashMap<>();
//                                chatBoxData.put("name", userName1 + ", " + userName2);
                                chatBoxData.put("lastMessage", "");
                                chatBoxData.put("lastMessageTimestamp", FieldValue.serverTimestamp());
                                chatBoxData.put("image_url", "");

                                // Create the "iamge" map with users' IDs
                                Map<String, String> imageMap = new HashMap<>();
                                imageMap.put(userID1, avatar1);
                                imageMap.put(userID2, avatar2);
                                chatBoxData.put("image_url", imageMap);

                                // Create the "name" map with users' IDs
                                Map<String, String> nameMap = new HashMap<>();
                                nameMap.put(userID1, userName1);
                                nameMap.put(userID2, userName2);
                                chatBoxData.put("name", nameMap);

                                // Create the "showed" map with users' IDs
                                Map<String, Boolean> showedMap = new HashMap<>();
                                showedMap.put(userID1, false);
                                showedMap.put(userID2, false);
                                chatBoxData.put("showed", showedMap);

                                // Add a new chat box document
                                firestore.collection("chatbox")
                                        .add(chatBoxData)
                                        .addOnSuccessListener(documentReference -> {
                                            String chatBoxId = documentReference.getId();

                                            // Fetch the full ChatBox object after creation
                                            firestore.collection("chatbox")
                                                    .document(chatBoxId)
                                                    .get()
                                                    .addOnSuccessListener(documentSnapshot -> {
                                                        if (documentSnapshot.exists()) {
                                                            // Create a ChatBox object from the document data
                                                            Map<String,String> imageUrls = (Map<String, String>) documentSnapshot.get("image_url");
                                                            ChatBox chatBox = documentSnapshot.toObject(ChatBox.class);
                                                            if (chatBox != null) {
                                                                chatBox.setImageUrl(imageUrls);
                                                                chatBox.setId(chatBoxId);
                                                                callback.onChatBoxRetrieved(chatBox);  // Pass the full ChatBox object
                                                            }
                                                        }
                                                    });
                                        });
                            });
                });
    }
    private void createAIChatBox(String userID) {
        firestore.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.exists() ? documentSnapshot.getString("Name") : userID;
                    String avatar = documentSnapshot.exists() ? documentSnapshot.getString("avatar") : null;

                    Map<String, Object> chatBoxData = new HashMap<>();
                    chatBoxData.put("lastMessage", "Xin chào bạn");
                    chatBoxData.put("lastMessageTimestamp", FieldValue.serverTimestamp());

                    // Create the "image" map with user's avatar and AI
                    Map<String, String> imageMap = new HashMap<>();
                    imageMap.put(userID, avatar);  // User avatar
                    imageMap.put("Chat bot", "");  // AI avatar
                    chatBoxData.put("image_url", imageMap);

                    // Create the "name" map with user's name and AI name
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put(userID, userName);
                    nameMap.put("Chat bot", "Trợ lý AI");
                    chatBoxData.put("name", nameMap);

                    // Create the "showed" map with users' IDs
                    Map<String, Boolean> showedMap = new HashMap<>();
                    showedMap.put(userID, true);
//                    showedMap.put("gemini-bot", true);
                    chatBoxData.put("showed", showedMap);

                    // Add a new chat box document for AI
                    firestore.collection("chatbox")
                            .add(chatBoxData)
                            .addOnSuccessListener(documentReference -> {
                                String chatBoxId = documentReference.getId();

                                // Fetch the full ChatBox object after creation
                                firestore.collection("chatbox")
                                        .document(chatBoxId)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot2 -> {
                                            if (documentSnapshot2.exists()) {
                                                // Create a ChatBox object from the document data
                                                Map<String,String> imageUrls = (Map<String, String>) documentSnapshot2.get("image_url");
                                                ChatBox chatBox = documentSnapshot2.toObject(ChatBox.class);
                                                if (chatBox != null) {
                                                    chatBox.setImageUrl(imageUrls);
                                                    chatBox.setId(chatBoxId);
                                                }
                                            }
                                        });
                            });
                });
    }
    public void checkAndCreateAIChatBox(String userID) {
        // Kiểm tra nếu có ChatBox AI chưa, nếu không thì tạo mới
        firestore.collection("chatbox")
                .get() // Lấy tất cả chatbox
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean aiChatBoxExists = false;

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Lấy map "name"
                        Map<String, String> nameMap = (Map<String, String>) documentSnapshot.get("name");
                        if (nameMap != null) {
                            // Kiểm tra xem "name" có chứa userID và "Chat bot" không
                            if (nameMap.containsKey(userID) && nameMap.containsKey("Chat bot")) {
                                String botName = nameMap.get("Chat bot");
                                // Kiểm tra nếu giá trị trong map là "Trợ lý AI"
                                if ("Trợ lý AI".equals(botName)) {
                                    aiChatBoxExists = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!aiChatBoxExists) {
                        // Nếu không tìm thấy, tạo mới ChatBox AI
                        createAIChatBox(userID);
                    }
                });
    }



    public boolean isAI(ChatBox chatBox) {
        // Kiểm tra xem trường "name" có chứa "Chat bot" không
        if (chatBox.getName() != null && chatBox.getName().containsKey("Chat bot")) {
            String botName = chatBox.getName().get("Chat bot");
            return botName != null && botName.equals("Trợ lý AI");
        }
        return false;
    }

    public void updateShowedToTrue(String chatBoxId, String userID1, String userID2, ChatBoxCallback2 callback) {
        // Update the "showed" field for the given chat box
        Map<String, Object> updates = new HashMap<>();
        updates.put("showed." + userID1, true);
        updates.put("showed." + userID2, true);

        firestore.collection("chatbox")
                .document(chatBoxId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // After update, fetch and return the updated ChatBox
                    firestore.collection("chatbox")
                            .document(chatBoxId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Map<String,String> imageUrls = (Map<String, String>) documentSnapshot.get("image_url");
                                    ChatBox chatBox = documentSnapshot.toObject(ChatBox.class);
                                    assert chatBox != null;
                                    chatBox.setImageUrl(imageUrls);
                                    chatBox.setId(chatBoxId);
                                    callback.onChatBoxRetrieved(chatBox);  // Pass the updated ChatBox object
                                }
                            });
                });
    }

    public interface ChatBoxCallback2 {
        void onChatBoxRetrieved(ChatBox chatBox);
    }

    public void updateAllShowedToTrue(String chatBoxId) {
        DocumentReference chatBoxRef = firestore.collection("chatbox").document(chatBoxId);

        chatBoxRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Lấy map của trường "showed"
                Map<String, Boolean> showedMap = (Map<String, Boolean>) documentSnapshot.get("showed");

                if (showedMap != null) {
                    // Đặt tất cả giá trị trong map thành true
                    for (String key : showedMap.keySet()) {
                        showedMap.put(key, true);
                    }
                    chatBoxRef.update("showed", showedMap);
                }
            }
        });
    }

    public void updateChatBoxName(String chatBoxID, Map<String, String> newNames, OnCompleteListener<Boolean> callback) {
        firestore.collection("chatbox").document(chatBoxID)
                .update("name", newNames)  // Cập nhật toàn bộ Map<String, String>
                .addOnCompleteListener(task -> {
                    // Tạo Task<Boolean> dựa trên kết quả của Firestore update
                    Task<Boolean> resultTask = Tasks.forResult(task.isSuccessful());
                    callback.onComplete(resultTask);  // Gọi callback với Task<Boolean>
                });
    }

    public void uploadChatBoxImage(Uri imageUri, String chatBoxID, UploadImageCallback callback) {
        // Reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chatBoxImages/" + chatBoxID + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString())))
                .addOnFailureListener(callback::onFailure);
    }

    public void updateChatBoxImage(String chatBoxID, String userID, String imageUrl, OnCompleteListener<Void> callback) {
        DocumentReference chatBoxRef = firestore.collection("chatbox").document(chatBoxID);

        chatBoxRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, String> imageMap = (Map<String, String>) documentSnapshot.get("image_url");

                if (imageMap == null) {
                    imageMap = new HashMap<>();
                }

                // Chỉ cập nhật nếu URL mới khác với URL cũ của userID
                if (!imageUrl.equals(imageMap.get(userID))) {
                    imageMap.put(userID, imageUrl);
                    chatBoxRef.update("image_url", imageMap).addOnCompleteListener(callback);
                }
            }
        });
    }


    public interface UploadImageCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }
}
