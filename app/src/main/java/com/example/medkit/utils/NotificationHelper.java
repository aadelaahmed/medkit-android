package com.example.medkit.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.medkit.model.NotificationModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationHelper {
    static String currentUserID;
    static String TAG = "notification";
    private static FirebaseAuth auth;

    public static void SendCommentNotification(final String messageText,
                                               final String targetUserID,
                                               final String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference rootPost = db.collection("Users/" + targetUserID + "/Notification");
        DocumentReference docRef = rootPost.document();
        String notiKey = docRef.getId();
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        if (!currentUserID.equals(targetUserID)) {
//            final FirebaseFirestore mfirebase = FirebaseFirestore.getInstance();
            if (!TextUtils.isEmpty(messageText)) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy h:mm a", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                String fullMessage = "has commented on your post " + messageText;
//                String id = mfirebase.collection("Users/" + targetUserID + "/Notification").document().getId();
//                Map<String, Object> notificationMessage = new HashMap<>();
//                notificationMessage.put("n_id", id);
//                notificationMessage.put("message", fullMessage);
//                notificationMessage.put("from", currentUserID);
//                notificationMessage.put("post_id", postId);
//                notificationMessage.put("is_read", false);
//                notificationMessage.put("time", currentDateandTime);
                NotificationModel notificationModel = new NotificationModel(currentUserID,
                        postId,
                        fullMessage,
                        false,
                        currentDateandTime,
                        notiKey);
                docRef.set(notificationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: " + targetUserID + " " + messageText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context,"Notification Failure",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onFailure: " + e.getMessage() + " " + targetUserID + " " + messageText);
                    }
                });
//                mfirebase.collection("Users/" + targetUserID + "/Notification").document(id)
//                        .update(notificationMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "onSuccess: " + targetUserID + " " + messageText);
//                    }
//                })
            }
        }
    }

    public static void update_read(String notification_id) {
        Map<String, Object> notMap = new HashMap<>();
        notMap.put("read", true);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        final FirebaseFirestore mfirebase = FirebaseFirestore.getInstance();
        mfirebase.collection("Users/" + currentUserID + "/Notification")
                .document(notification_id).update(notMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("notification", "read success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("notification", "read failure");
            }
        });
    }
}
