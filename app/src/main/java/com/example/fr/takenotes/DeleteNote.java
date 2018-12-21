package com.example.fr.takenotes;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
public class DeleteNote {
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    private String noteId;


    public DeleteNote(String id){
        noteId=id;
        // Initialize Firebase Auth
        mFirebaseAuth=FirebaseAuth.getInstance();
        // Inıtialize the Firebase Firestore
        db=FirebaseFirestore.getInstance();
        Delete();
    }

    private void Delete(){
        db.collection("notes").document(mFirebaseAuth.getCurrentUser().getUid()).
                collection("note_list").document(noteId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
