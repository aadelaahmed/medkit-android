package com.example.medkit.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FirestorePostAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {
    private static final String TAG = "FirestoreAdapter";
    private Query mQuery;
    private ListenerRegistration mListener;
    private List<DocumentSnapshot> mList = new ArrayList<>();

    public FirestorePostAdapter(Query mQuery) {
        this.mQuery = mQuery;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.d(TAG, "onEvent: " + e.getMessage());
            return;
        }
        Log.d(TAG, "onEvent:numChanges:" + queryDocumentSnapshots.getDocumentChanges().size());
        for (DocumentChange docChange : queryDocumentSnapshots.getDocumentChanges()) {
            switch (docChange.getType()) {
                case ADDED:
                    onDocumentAdded(docChange);
                    break;
                case MODIFIED:
                    onDocumentModified(docChange);
                    break;
            }
        }
    }


    protected void onDocumentAdded(DocumentChange change) {
        mList.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change) {
        mList.set(change.getOldIndex(), change.getDocument());
        notifyItemChanged(change.getOldIndex());
       /* if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mList.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mList.remove(change.getOldIndex());
            mList.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }*/
    }

    protected DocumentSnapshot getSnapshot(int position) {
        return mList.get(position);
    }

    protected void addNewDocument(DocumentChange docChange) {
        mList.add(0, docChange.getDocument());
        notifyItemInserted(0);
    }

    public void startListener() {
        if (mQuery != null && mListener == null) {
            mListener = mQuery.addSnapshotListener(this);
            Log.d(TAG, "startListener: " + mList.size());
        }
    }

    public void stopListener() {
        if (mListener != null) {
            mListener.remove();
            mListener = null;
        }
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
