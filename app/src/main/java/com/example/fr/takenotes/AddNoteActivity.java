package com.example.fr.takenotes;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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

import java.util.Date;
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
    Long mTime, mEditTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().setTitle(getString(R.string.app_bar_add));
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
                saveNoteToDb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNoteToDb() {
        DocumentReference noteRef=db.collection("notes").document(mFirebaseAuth.getCurrentUser().getUid()).
                collection("note_list").document();

        mTitle=mTitleEditText.getText().toString().trim();
        mNote=mNoteEditText.getText().toString().trim();
        if(mTitle.isEmpty()&&mNote.isEmpty()){
            Toast.makeText(this,getString(R.string.empty_save),Toast.LENGTH_SHORT).show();
//            return;
        }else{
            // Get the current timestamp
            mTime= new Date().getTime();
            // mEditTime=new Date().getTime();
            // Create a new note with a first and last name
            Map<String, Object> note = new HashMap<>();
            // note.put("user_id", mFirebaseAuth.getCurrentUser().getUid());
            note.put("title", mTitle);
            note.put("note", mNote);
            note.put("date", mTime);
            note.put("editDate", mTime);
            note.put("id",noteRef.getId());

            noteRef.set(note)
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
            // Go back to the MainActivity.
            finish();
        }

    }

    private boolean isChanged(){
        String title, note;
        title=mTitleEditText.getText().toString().trim();
        note=mNoteEditText.getText().toString().trim();
        if(title.isEmpty()&&note.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
            if (isChanged()){
                showAlertDialogUnsavedChanges();
            } else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showAlertDialogUnsavedChanges(){
        final AlertDialog alertDialog = new AlertDialog.Builder(AddNoteActivity.this).create();
        alertDialog.setTitle(getString(R.string.warning));
        alertDialog.setMessage(getString(R.string.quest_save_changes));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        saveNoteToDb();
                        alertDialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

}
