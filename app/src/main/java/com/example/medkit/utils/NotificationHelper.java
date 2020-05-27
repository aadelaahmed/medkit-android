package com.example.medkit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medkit.model.NotificationModel;
import com.example.medkit.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

    public static void SendCommentNotification(final String userName,
                                               final String messageText,
                                               final String targetUserID,
                                               final String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference rootPost = db.collection(User.USER_COLLECTION + "/" + targetUserID + "/" + NotificationModel.NOTIFICATION_COLLECTION);
        final DocumentReference docRef = rootPost.document();
        final String notiKey = docRef.getId();
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        if (!currentUserID.equals(targetUserID)) {
            Log.d(TAG, "SendCommentNotification: " + messageText + targetUserID + postId);
            String fullMessage = "has commented on your post";
            Log.d(TAG, "onEvent: " + userName);
            NotificationModel notificationModel = new NotificationModel(currentUserID,
                    postId,
                    fullMessage,
                    false,
                    notiKey,
                    userName
            );
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

        }
    }

    public static void update_read(String notification_id) {
        Map<String, Object> notMap = new HashMap<>();
        notMap.put("read", true);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        final FirebaseFirestore mfirebase = FirebaseFirestore.getInstance();
        mfirebase.collection(User.USER_COLLECTION + "/" + currentUserID + "/" + NotificationModel.NOTIFICATION_COLLECTION)
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
