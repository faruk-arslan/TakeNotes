package com.example.fr.takenotes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {
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
    Long mTime, mEditTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        // Initialize Firebase Auth
        mFirebaseAuth=FirebaseAuth.getInstance();
        // Inıtialize the Firebase Firestore
        db=FirebaseFirestore.getInstance();
        // Get the EditText Instances
        mTitleEditText=(EditText) findViewById(R.id.edit_text_title);
        mNoteEditText=(EditText) findViewById(R.id.edit_text_note);

        getIntentData();
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
                updateNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getIntentData(){
        if(getIntent().hasExtra("title_extra")&&getIntent().hasExtra("note_extra")
                &&getIntent().hasExtra("time_extra")&&getIntent().hasExtra("edit_time_extra")){

            mTitleEditText.setText(getIntent().getStringExtra("title_extra"));
            mNoteEditText.setText(getIntent().getStringExtra("note_extra"));
        }
    }

    private void updateNote(){
        final String title, note;
        title=mTitleEditText.getText().toString().trim();
        note=mNoteEditText.getText().toString().trim();

        final DocumentReference noteRef=db.collection("notes").document(mFirebaseAuth.getCurrentUser().getUid()).
                collection("note_list").document(getIntent().getStringExtra("note_id_extra"));

        noteRef.update("title", title, "note", note, "editDate", new Date().getTime())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        finish();
    }

}
