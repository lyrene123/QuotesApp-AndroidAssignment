package cs.dawson.myapplication.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import cs.dawson.myapplication.MainActivity;


/**
 * Created by laborlyrene on 2017-11-01.
 */

public class DBHelperUtil {


    private final String email = "user@droid.com";
    private final String password = "iloveandroid";
    private FirebaseAuth mFirebaseAuth;
    private boolean isAuthenticated;

    public DBHelperUtil(){
        this.isAuthenticated = false;
    }

    public void signInToFirebase(Activity activity){
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //sign in into firebase to retrieve data
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setIsAuthenticated(true);
                        }
                    }
                });
    }

    public boolean getIsAuthenticated(){
        return this.isAuthenticated;
    }

    public void setIsAuthenticated(boolean authenticated){
        this.isAuthenticated = authenticated;
    }


}

