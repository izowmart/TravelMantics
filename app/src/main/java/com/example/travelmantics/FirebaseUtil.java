package com.example.travelmantics;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FirebaseUtil {
    private static final String TAG = "FirebaseUtil";

    private static final int RC_SIGN_IN = 5432;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static ArrayList<TravelDeal> mData;
    public static FirebaseAuth auth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;
    private static ListActivity caller;
    public static boolean isAdmin;

    //This prevents instantiating the Firebase class it can only be instantiated from inside of our class
    private FirebaseUtil() {
    }

    public static void openFbReference(String ref, final ListActivity activityCaller) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference firstTimeRef = firebaseDatabase.getReference().child("traveldeals");
            auth = FirebaseAuth.getInstance();
            caller = activityCaller;
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }else{
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                        Toast.makeText(activityCaller.getBaseContext(), "Welcome Back!", Toast.LENGTH_SHORT).show();
                    }

                }
            };
            connectToFirebaseStorage();
        }
        mData = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);
    }

    private static void checkAdmin(String uid){
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administrators")
                .child(uid);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
                Log.d(TAG, "onChildAdded: You are an administrator");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }


    public static void attachStateListener() {
        auth.addAuthStateListener(authStateListener);
    }

    public static void dettachStateListener() {
        auth.removeAuthStateListener(authStateListener);
    }
    public static void connectToFirebaseStorage(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("travelDealsImages");
    }
}
