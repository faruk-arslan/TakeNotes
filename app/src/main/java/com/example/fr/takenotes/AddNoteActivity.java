package com.example.fr.takenotes;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    // TAG for AddNoteActivity class.
    private static String TAG=AddNoteActivity.class.getSimpleName();
    // Firebase Authentication provides backend services, easy-to-use SDKs,
    // and ready-made UI libraries to authenticate users to your app.
    private FirebaseAuth mFirebaseAuth;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    // Get the EditText references.
    EditText mTitleEditText, mNoteEditText;
    // String definitions for title and note.
    String mTitle, mNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        // Initialize Firebase Auth
        mFirebaseAuth=FirebaseAuth.getInstance();
        // Inıtialize the Firebase Firestore
        db=FirebaseFirestore.getInstance();
        // Get the EditText Instances
        mTitleEditText=(EditText) findViewById(R.id.edit_text_title);
        mNoteEditText=(EditText) findViewById(R.id.edit_text_note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_note,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.save_note:
                // Toast.makeText(this, "Menu is working", Toast.LENGTH_SHORT).show();
                saveNoteToDb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNoteToDb() {
        mTitle=mTitleEditText.getText().toString().trim();
        mNote=mNoteEditText.getText().toString().trim();

        // Create a new note with a first and last name
        Map<String, Object> note = new HashMap<>();
        // note.put("user_id", mFirebaseAuth.getCurrentUser().getUid());
        note.put("title", mTitle);
        note.put("note", mNote);

        // Add a new document with a generated ID
        db.collection("notes").document(mFirebaseAuth.getCurrentUser().getUid()).
                collection("note_list")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
}
