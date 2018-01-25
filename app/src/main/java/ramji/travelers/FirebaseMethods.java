package ramji.travelers;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.MathContext;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";


    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRefernece;
    private String userID;

    public FirebaseMethods(Context context){

        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRefernece = FirebaseStorage.getInstance().getReference();

        if (mAuth.getCurrentUser() != null){

            userID = mAuth.getCurrentUser().getUid();

        }
    }

    public void registerNewEmail(final String username, final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG,"created User with email and password: "+ task.isSuccessful());
                        if (!task.isSuccessful()){
                            Log.e(TAG,mContext.getString(R.string.account_creation_failed));
                        }else{
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG,"onComplete: authState changed: "+ userID);

                            myRef.child(mContext.getString(R.string.dbname_users))
                                    .child(userID)
                                    .child(mContext.getString(R.string.username))
                                    .setValue(username);

                            myRef.child(mContext.getString(R.string.dbname_users))
                                    .child(userID)
                                    .child(mContext.getString(R.string.field_emailId))
                                    .setValue(email);

                            Log.i(TAG,"added to database");
                        }
                    }
                });

    }

    private void sendVerificationEmail() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(mContext,mContext.getString(R.string.verification_Email_sent),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e(TAG,"Failed to send Email");
                    }
                }
            });
        }
    }
}
