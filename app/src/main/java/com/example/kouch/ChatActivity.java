package com.example.kouch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kouch.Model.ChatMessageModel;
import com.example.kouch.Model.ChatRoomModel;
import com.example.kouch.Model.User;
import com.example.kouch.adapter.ChatRecyclerAdapter;
import com.example.kouch.utils.AndroidUtil;
import com.example.kouch.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    User otherUser;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter adapter;
    String chatroomId;
    EditText messageInput;
    ImageButton backBtn;
    ImageButton cancelReplyButton;
    ImageButton sendMessageBtn;
    TextView otherUserFname;
    RecyclerView recyclerView;
    ImageView imageView;
    private TextView otherUserStatus;
    private ChatMessageModel messageToReplyTo;
    private TextView replyContextText;
    private LinearLayout replyContextContainer;
    private boolean isTyping = false;
    private long lastTypingTime = 0;
    private static final long TYPING_TIMEOUT = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserFromInten(getIntent()); // Initialize otherUser first

        // Check if otherUser is null
        if (otherUser == null) {
            throw new IllegalArgumentException("User object passed to ChatActivity is null");
        }

        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getId());

        messageInput = findViewById(R.id.message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUserFname = findViewById(R.id.other_User_Fname);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView=findViewById(R.id.profile_pic_image_view);
        replyContextText = findViewById(R.id.reply_context_text);
        replyContextContainer = findViewById(R.id.reply_context_container);
        otherUserStatus = findViewById(R.id.other_User_Status);

        FirebaseUtil.GetOtherProfilePicStorageRef(otherUser.getId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        updateUserStatus();

        cancelReplyButton = findViewById(R.id.cancel_reply_button);
        cancelReplyButton.setOnClickListener(v -> {
            clearReplyContext();
        });


        // Add a listener to update the status whenever it changes in the database
        FirebaseFirestore.getInstance().collection("users").document(otherUser.getId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.w("ChatActivity", "Listen failed.", e);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            otherUser = user;
                            updateUserStatus();
                        }
                    }
                });
        backBtn.setOnClickListener(v -> onBackPressed());

        otherUserFname.setText(otherUser.getFName());
        getOrCreateChatroomModel();
        setupChatRecyclerView();
        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message);
            // Reset typing status after sending message
            updateStatus("online");
        });

        // Listen for typing events
        messageInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                lastTypingTime = System.currentTimeMillis();
                if (!isTyping) {
                    isTyping = true;
                    updateStatus("typing...");
                }
            }
            return false;
        });
        new Thread(() -> {
            while (true) {
                long currentTime = System.currentTimeMillis();
                if (isTyping && (currentTime - lastTypingTime > TYPING_TIMEOUT)) {
                    runOnUiThread(() -> {
                        isTyping = false;
                        updateStatus("online");
                    });
                }
                try {
                    Thread.sleep(500); // Check every 0.5 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatRoomMessageReferens(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnMessageClickListener((message, view) -> {
            showContextMenu(message, view);
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (adapter.getItemCount() - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
    }
    private void updateUserStatus() {
        // Assuming the User model has a method to get the status
        String status = otherUser.getStatus(); // This could be a method like getStatus()
        otherUserStatus.setText(status);
    }
    private void showContextMenu(ChatMessageModel message, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.message_context_menu, popup.getMenu());

        // Remove certain options if the message is not sent by the current user
        if (!message.getSenderId().equals(FirebaseUtil.currentUserId())) {
            popup.getMenu().findItem(R.id.edit).setVisible(false);
            popup.getMenu().findItem(R.id.delete).setVisible(false);
            popup.getMenu().findItem(R.id.copy).setVisible(false);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.edit) {
                editMessage(message);
                return true;
            } else if (itemId == R.id.delete) {
                deleteMessage(message);
                return true;
            } else if (itemId == R.id.copy) {
                copyMessage(message);
                return true;

            } else if (itemId == R.id.reply) {
                replyToMessage(message);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void editMessage(ChatMessageModel message) {
        // Показать диалоговое окно или активность для редактирования сообщения
        showEditDialog(message);
    }

    private void showEditDialog(ChatMessageModel message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Message");
        EditText editText = new EditText(this);
        editText.setText(message.getMessage());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editedMessage = editText.getText().toString().trim();
                if (!editedMessage.isEmpty()) {
                    updateMessageInFirestore(message, editedMessage);
                } else {
                    Toast.makeText(ChatActivity.this, "Cannot save empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void updateMessageInFirestore(ChatMessageModel message, String editedMessage) {
        // Обновляем текст сообщения в объекте ChatMessageModel
        message.setMessage(editedMessage);

        // Обновляем документ в Firestore
        FirebaseUtil.getChatRoomMessageReferens(chatroomId)
                .document(message.getId())
                .set(message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatActivity.this, "Message updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Failed to update message", Toast.LENGTH_SHORT).show();
                            Log.e("updateMessage", "Failed to update message: " + message.getId(), task.getException());
                        }
                    }
                });
    }

    private void deleteMessage(ChatMessageModel message) {
        FirebaseUtil.getChatRoomMessageReferens(chatroomId)
                .document(message.getId()) // Use the correct ID here
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                        Log.i("deleteMessage", "Message deleted successfully: " + message.getId());
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to delete message", Toast.LENGTH_SHORT).show();
                        Log.e("deleteMessage", "Failed to delete message: " + message.getId(), task.getException());
                    }
                });
    }

    private void copyMessage(ChatMessageModel message) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", message.getMessage());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Message copied", Toast.LENGTH_SHORT).show();
    }
    private void replyToMessage(ChatMessageModel message) {
        messageToReplyTo = message;
        replyContextText.setText("Replying to: " + message.getMessage());
        replyContextContainer.setVisibility(View.VISIBLE);
    }

    private void clearReplyContext() {
        messageToReplyTo = null;
        replyContextContainer.setVisibility(View.GONE);
    }


    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);
                }
            }
        });
    }

    void sendMessageToUser(String message) {
        String messageId = FirebaseUtil.getChatRoomMessageReferens(chatroomId).document().getId();
        ChatMessageModel chatMessageModel;

        if (messageToReplyTo != null) {
            chatMessageModel = new ChatMessageModel(messageId, message, FirebaseUtil.currentUserId(), Timestamp.now(), messageToReplyTo.getId(), messageToReplyTo.getMessage());
            clearReplyContext();
        } else {
            chatMessageModel = new ChatMessageModel(messageId, message, FirebaseUtil.currentUserId(), Timestamp.now());
        }

        // Save chat message model
        FirebaseUtil.getChatRoomMessageReferens(chatroomId).document(messageId).set(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText("");
                        sendNotification(message);
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });

        chatRoomModel.setLastMessage(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage_user(message);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Replaces the default 'Back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    void sendNotification(String message){
        Log.d("sendNotification", "Sending notification with message: " + message);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("sendNotification", "Successfully retrieved current user details");
                User currentuser = task.getResult().toObject(User.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", currentuser.getFName());
                    notificationObj.put("body", message);
                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentuser.getId());
                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());
                    calldApi(jsonObject);
                } catch (Exception e) {
                    Log.e("sendNotification", "Failed to create JSON object", e);
                }
            } else {
                Log.e("sendNotification", "Failed to retrieve current user details", task.getException());
            }
        });
    }
    void calldApi(JSONObject jsonObject){
        Log.d("calldApi", "Calling API with JSON: " + jsonObject.toString());
        MediaType JSON = MediaType.get("application/json");

        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer d0e75ec6a1a29969659472b6b2cbe6ab1f78daf8")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("calldApi", "API call failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("calldApi", "API call succeeded: " + response.body().string());
                } else {
                    Log.e("calldApi", "API call failed with response: " + response.body().string());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Perform any additional tasks here if needed before finishing the activity
        super.onBackPressed();  // This will finish the activity and go back to the previous activity
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("online");
    }
    @Override
    protected void onPause(){
        super.onPause();
        updateStatus("offline");
    }

    private void updateStatus(String status) {
        FirebaseUtil.currentUserDetails().update("status", status)
                .addOnSuccessListener(aVoid -> Log.d("StatusUpdate", "User status updated to " + status))
                .addOnFailureListener(e -> Log.w("StatusUpdate", "Error updating user status", e));
    }

}
