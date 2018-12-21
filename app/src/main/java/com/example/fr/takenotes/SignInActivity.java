package com.example.fr.takenotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
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
    // Google Sign-In is a secure authentication system that reduces
    // the burden of login for your users, by enabling them to sign
    // in with their Google Account.
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN=9001;
    // Firebase Authentication provides backend services, easy-to-use SDKs,
    // and ready-made UI libraries to authenticate users to your app.
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mFirebaseAuth=FirebaseAuth.getInstance();

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
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText(R.string.google_sign_in);

//        if(mFirebaseAuth.getCurrentUser()!=null){
//            Intent mainActivityIntent=new Intent(SignInActivity.this,
//                    MainActivity.class);
//            startActivity(mainActivityIntent);
//        }
//        updateUI(mFirebaseAuth.getCurrentUser());

        // Set the buttons On Click Listeners.
        signInButton.setOnClickListener(this);
//        makeInfoDialog();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        updateUI(mFirebaseAuth.getCurrentUser());
//    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            default:
                return;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
//             updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            // updateUI(null);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.main_layout_sign_in),
                                    getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser account) {
        if(account!=null){
            Intent mainActivityIntent=new Intent(SignInActivity.this,
                    MainActivity.class);
            startActivity(mainActivityIntent);
        }
        else{
            Toast.makeText(this,getString(R.string.login_fail),
                    Toast.LENGTH_SHORT).show();
        }
    }

//    private void makeInfoDialog(){
//        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle(getString(R.string.warning));
//        alertDialog.setMessage(getString(R.string.warning_desc));
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay),
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int i) {
//                        alertDialog.dismiss();
//                    }
//                });
//        alertDialog.show();
//    }

}
