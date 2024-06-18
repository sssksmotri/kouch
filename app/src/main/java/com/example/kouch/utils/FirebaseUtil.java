package com.example.kouch.utils;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class FirebaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static CollectionReference allCollectionReferens(){
    return FirebaseFirestore.getInstance().collection("users");
    }
    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }
        else{
            return userId2+"_"+userId1;
        }
    }
    public static CollectionReference getChatRoomMessageReferens(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }
    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
           return allCollectionReferens().document(userIds.get(1));
        }else {
           return allCollectionReferens().document(userIds.get(0));
        }

    }
    public static String timestampToString(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return sdf.format(timestamp.toDate());
    }
    public static void Logout(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference GetCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
    public static StorageReference GetOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    public static Task<DocumentSnapshot> getUserDetails(String userId) {
        return FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get();
    }


}
