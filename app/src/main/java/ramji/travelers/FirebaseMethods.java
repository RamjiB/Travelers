package ramji.travelers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ramji.travelers.Utils.FilePaths;
import ramji.travelers.Utils.Photo;
import ramji.travelers.Utils.Users;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";


    private final Context mContext;
    private final DatabaseReference myRef;
    private final FirebaseAuth mAuth;
    private final StorageReference mStorageRefernece;
    private String userID;

    private double mPhotoUploadProgress;

    public FirebaseMethods(Context context) {

        mContext = context;
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRefernece = FirebaseStorage.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {

            userID = mAuth.getCurrentUser().getUid();

        }
    }

    public int getImageCount(DataSnapshot datasnapshot) {

        int count = 0;
        for (DataSnapshot ds : datasnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;
        }

        return count;
    }

    public void registerNewEmail(final String username, final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "created User with email and password: " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.e(TAG, mContext.getString(R.string.account_creation_failed));
                        } else {
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: authState changed: " + userID);

                            Users users = new Users();
                            users.setUsername(username);
                            users.setUser_id(userID);
                            users.setAbout_me("");
                            users.setCity("");
                            users.setProfile_image_path("");

                            myRef.child(mContext.getString(R.string.dbname_users))
                                    .child(userID)
                                    .setValue(users);

                            Log.i(TAG, "added to database");
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
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, mContext.getString(R.string.verification_Email_sent),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to send Email");
                    }
                }
            });
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, int count,
                               final String imgUrl, final String location, final ProgressBar progressBar) {

        Log.d(TAG, "uploadNewPhoto: attempting to upload a new photo");

        FilePaths filePaths = new FilePaths();

        //case 1: new photo

        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageRefernece
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            Uri file = Uri.fromFile(new File(imgUrl));

            UploadTask uploadTask;
            uploadTask = storageReference.putFile(file);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String fileType = taskSnapshot.getMetadata().getContentType();
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, mContext.getString(R.string.photo_upload_success),
                            Toast.LENGTH_SHORT).show();

                    // add the new photo to 'photos' node and 'users_photo' node

                    addPhotoToDatabase(caption, location, firebaseUrl.toString(), fileType);

                    //navigate to the main feed so the user cans ee their photo
                    progressBar.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: photo upload failed");
                    Toast.makeText(mContext, mContext.getString(R.string.photo_upload_failed),
                            Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, mContext.getString(R.string.photo_upload_progress) + String.format(Locale.ENGLISH, "%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + " % done");
                }
            });

        }
    }

    public void uploadProfilePhoto(final String aboutMe, final String profileName,
                                   final String imgUrl, final String location, final ProgressBar progressBar) {

        //case 2: new profile photo

        FilePaths filePaths = new FilePaths();

        Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = mStorageRefernece
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

        Uri file = Uri.fromFile(new File(imgUrl));

        UploadTask uploadTask;
        uploadTask = storageReference.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                Toast.makeText(mContext, mContext.getString(R.string.photo_upload_success), Toast.LENGTH_SHORT).show();
                addProfilePhotoToDatabase(aboutMe, profileName, firebaseUrl.toString(), location);

                progressBar.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("fromSignUpActivity", true);
                mContext.startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "onFailure: photo upload failed");
                Toast.makeText(mContext, mContext.getString(R.string.photo_upload_failed), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                if (progress - 15 > mPhotoUploadProgress) {
                    Toast.makeText(mContext, mContext.getString(R.string.photo_upload_progress) +
                            String.format(Locale.ENGLISH, "%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }
                Log.d(TAG, "onProgress: upload progress: " + progress + " % done");
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addSavedPhotos(String caption, String location, String url, boolean favourite,
                               String photoKey, String fileType) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database");

        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setLocation(location);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(photoKey);
        photo.setFavourite(favourite);
        photo.setImageFile(fileType);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_saved_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(photoKey).setValue(photo);
    }


    public void addProfilePhotoToDatabase(String aboutMe, String profileName,
                                          String imgUrl, String location) {

        Log.d(TAG, "addProfilePhotoToDatabase: adding photo to database");

        Users users = new Users();
        users.setUsername(profileName);
        users.setUser_id(userID);
        users.setAbout_me(aboutMe);
        users.setCity(location);
        users.setProfile_image_path(imgUrl);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(users);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addPhotoToDatabase(String caption, String location, String url, String fileType) {


        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setLocation(location);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);
        photo.setImageFile(fileType);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(newPhotoKey).setValue(photo);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getTimeStamp() {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        return sdf.format(new Date());

    }

}
