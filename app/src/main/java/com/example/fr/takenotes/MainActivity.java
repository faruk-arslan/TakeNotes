package com.example.fr.takenotes;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    // TAG for MainActivity class.
    private static String TAG=MainActivity.class.getSimpleName();
    // Firebase Authentication provides backend services, easy-to-use SDKs,
    // and ready-made UI libraries to authenticate users to your app.
    private FirebaseAuth mFirebaseAuth;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    // Get the adapter and the recycler view references.
    private NotesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private ArrayList<Note> noteArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mFirebaseAuth=FirebaseAuth.getInstance();
        // Inıtialize the Firebase Firestore
        db=FirebaseFirestore.getInstance();
        // Create an ArrayList object
        noteArrayList=new ArrayList<>();
        // Get the floating action button instance from layout and
        // set it's onClickListener.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent addNoteIntent=new Intent(MainActivity.this,AddNoteActivity.class);
                startActivity(addNoteIntent);
            }
        });

        mRecyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);
        mAdapter= new NotesAdapter(noteArrayList,this);

        if(mFirebaseAuth.getCurrentUser()!=null){
            getDataFromDB();
        }else{
            Intent signInIntent=new Intent(MainActivity.this,SignInActivity.class);
            startActivity(signInIntent);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        // If there is no signed in user go to SignInActivity.
        if (currentUser==null){
            Intent signInIntent=new Intent(MainActivity.this,SignInActivity.class);
            startActivity(signInIntent);
        }
        else{
            Toast.makeText(this, "We have logged in user: "+currentUser.getDisplayName(),
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG,"onStart ------> with current user");
            getDataFromDB();
        }
    }

    private void getDataFromDB(){

        final DocumentReference docRef = db.collection("notes").
                document(mFirebaseAuth.getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


        db.collection("notes").document(mFirebaseAuth.getCurrentUser().getUid()).
                collection("note_list")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Note note;
                            noteArrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                note=new Note(document.getString("title"),document.getString("note"),
                                        document.getString("id"), document.getLong("date"),
                                        document.getLong("editDate"));
                                Log.d(TAG, document.getId() + " => " + note.getmTitle()+"-"+note.getmNote());
                                noteArrayList.add(note);
                                Log.d(TAG, "---------"+noteArrayList.size());
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
