package com.example.fr.takenotes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG="SignInActivity";
    SignInButton signInButton;
    Button signOutButton;
    // Google Sign-In is a secure authentication system that reduces
    // the burden of login for your users, by enabling them to sign
    // in with their Google Account.
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN=9001;
    // Firebase Authentication provides backend services, easy-to-use SDKs,
    // and ready-made UI libraries to authenticate users to your app.
    private FirebaseAuth mFirebaseAuth;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mFirebaseAuth=FirebaseAuth.getInstance();

        // Inıtialize the Firebase Firestore
        db=FirebaseFirestore.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get the Sign In and Sign Out buttons from layout.
        signInButton= (SignInButton) findViewById(R.id.sign_in_button);
        signOutButton=(Button) findViewById(R.id.sign_out_button);

        // Set the buttons On Click Listeners.
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        //----------------------------------------
        // Check which request we're responding to
        if (requestCode == RC_SIGN_IN) {
            // The user picked a contact.
            // The Intent's data Uri identifies which contact was selected.
            //----------------------------------------
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout_sign_in), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser account) {
        if(account!=null){
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
            Intent mainActivityIntent=new Intent(SignInActivity.this,MainActivity.class);
            //isNewUser();
            addUserToDb();
            startActivity(mainActivityIntent);
        } else{
            Toast.makeText(this, "Failed when Logging In", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks is logged in user is a new user or existing user.
     * If it's a new user add the user to database otherwise don't.
     */
    public void isNewUser(){
        // db collection path for reading operations.
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // String for assign the emails which are read from database.
                            String email;
                            Log.d(TAG, "Task is successfull (loop is not started yet.)");
                            Log.d(TAG, "///"+task.getResult());
                            // QuerySnapshot document=task.getResult();
                            if(task.getResult()==null){
                                addUserToDb();
                            }else{
                                // Loop for query each email.
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    // Log.d(TAG,"Email: "+document.get("email"));
                                    email=document.get("email").toString();
                                    Log.d(TAG, document.getId() + " ===> " + email);
                                    // If it's not a new user don't add the user to database
                                    if(checkUser(email)){
                                        Log.d(TAG, "User is already exist." + email);
                                        break;
                                    }
                                    // If it's a new user add the user to database
                                    else {
                                        Log.d(TAG, "Adding new user..." + email);
                                        addUserToDb();
                                    }
                                }
                            }


                            Log.d(TAG, "Task is successfull (out of the for loop)");
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     * Adds the user to database.
     */
    public void addUserToDb(){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", mFirebaseAuth.getCurrentUser().getUid());
        user.put("name", mFirebaseAuth.getCurrentUser().getDisplayName());
        user.put("email", mFirebaseAuth.getCurrentUser().getEmail());

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
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

    /**
     * Checks two Strings(email). Returns true if Strings are equal, otherwise returns false.
     * @param email1
     * @return
     */
    public boolean checkUser(String email1){
        return email1.equals(mFirebaseAuth.getCurrentUser().getEmail());
    }

}
